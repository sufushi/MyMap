package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rdc.mymap.R;
import com.rdc.mymap.database.DataBaseHelper;
import com.rdc.mymap.utils.HttpUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.xm.weidongjian.progressbuttonlib.ProgressButton;

/**
 * Created by wsoyz on 2017/4/22.
 */

public class PhotoActivity extends Activity implements View.OnClickListener{
    public final static int TYPE_COLLECTION_PHOTO = 1;
    public final static int TYPE_USER_PHOTO = 2;
    public final static int TYPE_URL_PHOTO = 3;
    private final static int OK = 1;
    private final static int NOTOK = 2;
    private final static String TAG = "PhotoActivity";

    private ImageView mBackImageView;
    private ImageView mainImageView;
    private TextView mShareTextView;
    private TextView mTitleTextView;
    private Intent intent;
    private int type = 0;
    private String path;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.getData().getInt("case")) {
                case OK: {
                    if(path != null){
                        Bitmap bitmap = BitmapFactory.decodeFile(path);
                        if(bitmap != null) {
                            mainImageView.setImageBitmap(bitmap);
                        }
                    }
                    break;
                }
                case NOTOK: {
                    break;
                }
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_photo);
        init();
    }
    private void init(){
        mainImageView = (ImageView) findViewById(R.id.iv_main);
        mShareTextView = (TextView) findViewById(R.id.tv_share);
        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mTitleTextView = (TextView) findViewById(R.id.tv_title);
        mBackImageView.setOnClickListener(this);
        intent = getIntent();
        type = intent.getIntExtra("type",0);
        switch (intent.getIntExtra("type",0)){
            case TYPE_COLLECTION_PHOTO:
                mShareTextView.setOnClickListener(this);
                setLocalPhoto();
                break;
            case TYPE_USER_PHOTO:
                mShareTextView.setText("");
                mTitleTextView.setText("头像");
                setDataBasePhoto();
                break;
            case TYPE_URL_PHOTO:
                mTitleTextView.setText("头像");
                mShareTextView.setText("");
                setURLPhoto();
                break;
            default:
                break;
        }
    }
    @Override
    protected void onResume() {
        init();
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_share:
                if(type == TYPE_COLLECTION_PHOTO) share();
                break;
            default:
                break;
        }
    }
    private void share(){
        shareMsg("皮皮虾出行","msgTitle","msg",path);
    }
    private void setLocalPhoto(){
        path = intent.getStringExtra("path");
        if(path != null){
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            if(bitmap != null) {
                mainImageView.setImageBitmap(bitmap);
            }
        }
    }
    private void setDataBasePhoto(){
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this,"Data.db",1);
        Bitmap bitmap = dataBaseHelper.getUserPhotoToBitmap(intent.getIntExtra("id",-1));
        if(bitmap != null){
            mainImageView.setImageBitmap(bitmap);
        }else{
            mainImageView.setImageResource(R.drawable.pikaqiu);
        }
    }
    private void setURLPhoto(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = HttpUtil.getPhpop(intent.getIntExtra("id",0));
                if(bitmap != null){
                    Log.d(TAG," setURLPhoto bitmap != null");
                    File destDir = new File(Environment.getExternalStorageDirectory()+"/CollectionPhoto");
                    if (!destDir.exists()) {
                        destDir.mkdirs();
                    }
                    File file = new File(destDir, "share.jpg");
                    path =  file.getPath();
                    FileOutputStream out = null;
                    try {
                        out = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                        out.flush();
                        out.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Bundle bundle = new Bundle();
                    bundle.putInt("case", OK);
                    Message message = new Message();
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }
    public void shareMsg(String activityTitle, String msgTitle, String msgText,
                         String imgPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (imgPath == null || imgPath.equals("")) {
            intent.setType("text/plain"); // 纯文本
        } else {
            File f = new File(imgPath);
            if (f != null && f.exists() && f.isFile()) {
                intent.setType("image/jpg");
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }
        }
//        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
//        intent.putExtra(Intent.EXTRA_TEXT, msgText);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, activityTitle));
    }
}
