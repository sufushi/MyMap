package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rdc.mymap.R;
import com.rdc.mymap.config.SharePreferencesConfig;
import com.rdc.mymap.config.URLConfig;
import com.rdc.mymap.database.DataBaseHelper;
import com.rdc.mymap.model.UserObject;
import com.rdc.mymap.utils.HttpUtil;
import com.rdc.mymap.view.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wsoyz on 2017/4/8.
 */

public class DetailsActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "DetailsActivity";
    private static final int OK = 1;
    private static final int NOTOK = 2;
    private static final int DIALOG_NAME = 1;
    private static final int DIALOG_AREA = 2;
    private static final int DIALOG_PHONE = 3;
    private static final int DIALOG_SIGN = 4;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    
    private TextView mLogoutTextView;
    private TextView mUserNameTextView;
    private LinearLayout mUserNameLinearlayout;
    private TextView mAreaTextView;
    private LinearLayout mAreaLinearLayout;
    private TextView mSignatureTextView;
    private LinearLayout mSignatureLinearLayout;
    private TextView mPhoneTextView;
    private LinearLayout mPhoneLinearLayout;
    private TextView mMaleTextView;
    private LinearLayout mMaleLinearLayout;
    private CircleImageView mPhotoCircleImageView;
    private ImageView mBackImageView;
    private UserObject userObject;
    private DataBaseHelper mDataBaseHelper;
    private boolean male = true;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.getData().getInt("case", 0)) {
                case OK:
                    Toast.makeText(DetailsActivity.this, msg.getData().getString("message"), Toast.LENGTH_SHORT).show();
                    break;
                case NOTOK:
                    break;
                default:
                    Toast.makeText(DetailsActivity.this, msg.getData().getString("message"), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_details);
        init();
    }

    @Override
    protected void onResume() {
        Bitmap bm = mDataBaseHelper.getPhotoToBitmap(mPreferences.getInt(SharePreferencesConfig.ID_INT, 0));
        if (bm != null) mPhotoCircleImageView.setImageBitmap(bm);
        else mPhotoCircleImageView.setImageResource(R.drawable.pikaqiu);
        super.onResume();
    }

    private void init() {
        mPreferences = getSharedPreferences("main", MODE_PRIVATE);
        if (!mPreferences.getBoolean(SharePreferencesConfig.ISLOGIN_BOOLEAN, false)) {
            finish();
        }
        mDataBaseHelper = new DataBaseHelper(this, "Data.db", 1);
        userObject = mDataBaseHelper.getUserObjict(mPreferences.getInt(SharePreferencesConfig.ID_INT, 0));

        mLogoutTextView = (TextView) findViewById(R.id.tv_logout);
        mLogoutTextView.setOnClickListener(this);

        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mBackImageView.setOnClickListener(this);
        mPhotoCircleImageView = (CircleImageView) findViewById(R.id.civ_photo);
        mPhotoCircleImageView.setOnClickListener(this);

        Bitmap bm = mDataBaseHelper.getPhotoToBitmap(mPreferences.getInt(SharePreferencesConfig.ID_INT, 0));
        if (bm != null) mPhotoCircleImageView.setImageBitmap(bm);
        else mPhotoCircleImageView.setImageResource(R.drawable.pikaqiu);

        mUserNameTextView = (TextView) findViewById(R.id.tv_username);
        mUserNameTextView.setText(userObject.getUsername());

        mAreaTextView = (TextView) findViewById(R.id.tv_area);
        mAreaTextView.setText(userObject.getaddress());

        mSignatureTextView = (TextView) findViewById(R.id.tv_sign);
        mSignatureTextView.setText(userObject.getSignature());

        mPhoneTextView = (TextView) findViewById(R.id.tv_phonenumber);
        mPhoneTextView.setText(userObject.getPhoneNumber());

        mMaleTextView = (TextView) findViewById(R.id.tv_male);
        if(userObject.getGender() == 1)mMaleTextView.setText("男");
        else mMaleTextView.setText("女");
        mUserNameLinearlayout = (LinearLayout) findViewById(R.id.ll_username);
        mUserNameLinearlayout.setOnClickListener(this);
        mAreaLinearLayout = (LinearLayout) findViewById(R.id.ll_area);
        mAreaLinearLayout.setOnClickListener(this);
        mSignatureLinearLayout = (LinearLayout) findViewById(R.id.ll_sign);
        mSignatureLinearLayout.setOnClickListener(this);
        mPhoneLinearLayout = (LinearLayout) findViewById(R.id.ll_phonenumber);
        mPhoneLinearLayout.setOnClickListener(this);
        mMaleLinearLayout = (LinearLayout) findViewById(R.id.ll_male);
        mMaleLinearLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.civ_photo:
                startPhotoDialogActivity();
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_logout:
                LoginOut();
                finish();
                break;
            case R.id.ll_area:
                areaDialog();
                break;
            case R.id.ll_sign:
                signDialog();
                break;
            case R.id.ll_phonenumber:
                phoneDialog();
                break;
            case R.id.ll_male:
                maleDialog();
                break;
            default:
                break;
        }
    }
    {

    }
    private void change() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences mPreferences = getSharedPreferences("main", MODE_PRIVATE);
                Map<String, String> params = new HashMap<String, String>();
                mDataBaseHelper = new DataBaseHelper(DetailsActivity.this, "Data.db", 1);
                UserObject userObject = mDataBaseHelper.getUserObjict(mPreferences.getInt(SharePreferencesConfig.ID_INT, -1));
                params.put("gender", male + "");
                if(male)userObject.setGender(1);
                else userObject.setGender(0);
                params.put("address", mAreaTextView.getText().toString());
                userObject.setaddress(mAreaTextView.getText().toString());
                params.put("signature", mSignatureTextView.getText().toString());
                userObject.setSignature(mSignatureTextView.getText().toString());
                params.put("phoneNumber", mPhoneTextView.getText().toString());
                userObject.setPhoneNumber(mPhoneTextView.getText().toString());
                String jsonString = HttpUtil.submitPostDataWithCookie(params, mPreferences.getString(SharePreferencesConfig.COOKIE_STRING, ""), URLConfig.ACTION_CHANGE);
                if (jsonString.equals("")) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("case", NOTOK);
                    bundle.putString("message", "网络错误");
                    Message message = new Message();
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    if (jsonObject.isNull("code")) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("case", NOTOK);
                        bundle.putString("message", jsonObject.getString("错误"));
                        Message message = new Message();
                        message.setData(bundle);
                        mHandler.sendMessage(message);
                    } else {
                        if (jsonObject.getInt("code") == 0) {
                            Log.d(TAG, "ERROR code:" + jsonObject.getInt("code") + ". message:" + jsonObject.getString("message"));
                            Bundle bundle = new Bundle();
                            bundle.putInt("case", OK);
                            bundle.putString("message", "成功修改！");
                            Message message = new Message();
                            message.setData(bundle);
                            mHandler.sendMessage(message);
                            mDataBaseHelper.saveUser(userObject);
                        } else {
                            Log.d(TAG, "ERROR code:" + jsonObject.getInt("code") + ". message:" + jsonObject.getString("message"));
                            Bundle bundle = new Bundle();
                            bundle.putString("message", jsonObject.getString("message"));
                            Message message = new Message();
                            message.setData(bundle);
                            mHandler.sendMessage(message);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void startPersonCenterActivity() {
        Intent intent = new Intent(DetailsActivity.this, PersonCenterActivity.class);
        startActivity(intent);
    }

    private void startPhotoDialogActivity() {
        Intent intent = new Intent(DetailsActivity.this, DetailsPhotoActivity.class);
        startActivity(intent);
    }

    private void phoneDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle("修改手机号"); //设置标题
        builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
        final EditText editText = new EditText(this);
        InputFilter[] filters = {new InputFilter.LengthFilter(11)};
        editText.setFilters(filters);
        editText.addTextChangedListener(new SearchWather(editText));
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setText("1");
        editText.setSelection(editText.getText().toString().length());
        builder.setView(editText);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); //关闭dialog
                mPhoneTextView.setText(editText.getText());
                change();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //参数都设置完成了，创建并显示出来
        builder.create().show();
    }
    private void areaDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle("修改地区"); //设置标题
        builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
        final EditText editText = new EditText(this);
        InputFilter[] filters = {new InputFilter.LengthFilter(15)};
        editText.setFilters(filters);
        builder.setView(editText);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); //关闭dialog
                mAreaTextView.setText(editText.getText());
                change();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //参数都设置完成了，创建并显示出来
        builder.create().show();
    }
    private void signDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle("修改签名"); //设置标题
        builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
        final EditText editText = new EditText(this);
        InputFilter[] filters = {new InputFilter.LengthFilter(255)};
        editText.setFilters(filters);
        builder.setView(editText);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); //关闭dialog
                mSignatureTextView.setText(editText.getText());
                change();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //参数都设置完成了，创建并显示出来
        builder.create().show();
    }
    private void maleDialog(){
        final String items[]={"男","女"};
        final boolean tem = male;
        AlertDialog.Builder builder=new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle("提示"); //设置标题
        builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
        builder.setSingleChoiceItems(items,0,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dialog.dismiss();
                if (which == 0)male = true;
                else  male = false;
            }
        });
        builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(male)  mMaleTextView.setText("男");
                else mMaleTextView.setText("女");
                change();
            }
        });
        builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                male = tem;
                if(tem)  mMaleTextView.setText("男");
                else mMaleTextView.setText("女");
            }
        });
        builder.create().show();
    }
    private void LoginOut() {
        mEditor = mPreferences.edit();
        mEditor.clear();
        mEditor.commit();
        mDataBaseHelper.clear();
    }
    class SearchWather implements TextWatcher{


        //监听改变的文本框
        private EditText editText;


        public SearchWather(EditText editText){
            this.editText = editText;
        }

        @Override
        public void onTextChanged(CharSequence ss, int start, int before, int count) {
            switch (editText.getText().toString().length()){
                case 0:
                    editText.setText("1");
                    editText.setSelection(editText.getText().toString().length());
                    break;
                case 2:
                    if(ss.charAt(count) == '3' || ss.charAt(count) == '4' || ss.charAt(count) == '5' || ss.charAt(count) == '7' || ss.charAt(count) == '8' );
                    else editText.setText("1");
                    editText.setSelection(editText.getText().toString().length());
                    break;
                default:
                    break;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,int after) {

        }

    }

}
