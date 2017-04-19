package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.rdc.mymap.R;
import com.rdc.mymap.config.SharePreferencesConfig;
import com.rdc.mymap.database.DataBaseHelper;
import com.rdc.mymap.utils.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by wsoyz on 2017/4/18.
 */

public class PhotoDialogActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "PhotoDialogActivity";
    private static final String SUCCESS = "成功修改图片!";
    private Button mTakeButton;
    private Button mPickButton;
    private Button mCancelButton;
    private DataBaseHelper mDataBaseHelper;
    private SharedPreferences mSharedPreferences;
    private static final String IMAGE_FILE_NAME = "image.jpg";

    /** 请求码 */
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                default:
                    Toast.makeText(PhotoDialogActivity.this, msg.getData().getString("message"), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_photo_dialog);
        init();
    }



    private void init(){
        mTakeButton = (Button)findViewById(R.id.bt_take_photo);
        mTakeButton.setOnClickListener(this);
        mPickButton = (Button)findViewById(R.id.bt_pick_photo);
        mPickButton.setOnClickListener(this);
        mCancelButton = (Button)findViewById(R.id.bt_cancel);
        mCancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_take_photo:
                Intent intentFromCapture = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                // 判断存储卡是否可以用，可用进行存储
                String state = Environment
                        .getExternalStorageState();
                if (state.equals(Environment.MEDIA_MOUNTED)) {
                    File path = Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                    File file = new File(path, IMAGE_FILE_NAME);
                    if (file.exists()) {
                        file.delete();
                    }
                    intentFromCapture.putExtra(
                            MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(file));
                }

                startActivityForResult(intentFromCapture,
                        CAMERA_REQUEST_CODE);
                break;
            case R.id.bt_pick_photo:
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                startActivityForResult(intent, IMAGE_REQUEST_CODE);
                break;
            case R.id.bt_cancel:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 结果码不等于取消时候
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case IMAGE_REQUEST_CODE :
                    if (data != null)
                        startPhotoZoom(data.getData());
                    break;
                case CAMERA_REQUEST_CODE :
                    // 判断存储卡是否可以用，可用进行存储
                    String state = Environment.getExternalStorageState();
                    if (state.equals(Environment.MEDIA_MOUNTED)) {
                        File path = Environment
                                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                        File tempFile = new File(path, IMAGE_FILE_NAME);
                        startPhotoZoom(Uri.fromFile(tempFile));
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case RESULT_REQUEST_CODE : // 图片缩放完成后
                    if (data != null) {
                        getImageToView(data);
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESULT_REQUEST_CODE);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     *
     */
    private void getImageToView(final Intent data) {
        final Bundle extras = data.getExtras();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    mDataBaseHelper = new DataBaseHelper(PhotoDialogActivity.this,"Data.db",null,1);
                    mSharedPreferences = getSharedPreferences("main", MODE_PRIVATE);
                    mDataBaseHelper.savePhoto(mSharedPreferences.getInt(SharePreferencesConfig.ID_INT,-1),photo);
                    String jsonString = HttpUtil.submitPostPhoto(mDataBaseHelper.getPhotoToByte(mSharedPreferences.getInt(SharePreferencesConfig.ID_INT,-1)),mSharedPreferences.getString(SharePreferencesConfig.COOKIE_STRING,""));
                    if (jsonString.equals("")) {
                        Bundle bundle = new Bundle();
                        bundle.putString("message","网络错误");
                        Message message = new Message();
                        message.setData(bundle);
                        mHandler.sendMessage(message);
                        return;
                    }
                    Log.d(TAG,"jsonString--"+jsonString);
                    try {
                        JSONObject jsonObject = new JSONObject(jsonString);
                        if (jsonObject.getInt("code") == 0){
                            Bundle bundle = new Bundle();
                            bundle.putString("message",SUCCESS);
                            Message message = new Message();
                            message.setData(bundle);
                            mHandler.sendMessage(message);
                            finish();
                        }else{
                            Bundle bundle = new Bundle();
                            bundle.putString("message",jsonObject.getString("message"));
                            Message message = new Message();
                            message.setData(bundle);
                            mHandler.sendMessage(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }
}
