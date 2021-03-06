package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
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
import com.rdc.mymap.config.SharePreferencesConfig;
import com.rdc.mymap.config.URLConfig;
import com.rdc.mymap.database.DataBaseHelper;
import com.rdc.mymap.model.UserObject;
import com.rdc.mymap.utils.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.xm.weidongjian.progressbuttonlib.ProgressButton;

/**
 * Created by wsoyz on 2017/4/8.
 */

public class LoginActivity extends Activity implements View.OnClickListener, TextView.OnEditorActionListener {

    private final static String TAG = "LoginActivity";
    private final static int STATE_NETWORK = 1;

    private final static int STATE_REGISTER = 1;
    private final static int STATE_LOGIN = 2;


    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private ImageView mBackImageView;
    private TextView mForgetTextView;
    private TextView mRegisterTextView;
    private EditText mUserNameEditText;
    private EditText mPasswordEditText;
    private ProgressButton mEnterProgressButton;
    private DataBaseHelper mDataBaseHelper;
    private int i = 0;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case STATE_NETWORK :
                    mUserNameEditText.setEnabled(true);
                    mPasswordEditText.setEnabled(true);
                    mEnterProgressButton.animError();
                    mEnterProgressButton.setText("登录");
                    Toast.makeText(LoginActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    mUserNameEditText.setEnabled(true);
                    mPasswordEditText.setEnabled(true);
                    switch (msg.getData().getInt("case")){
                        case STATE_LOGIN:
                            mEnterProgressButton.animError();
                            mEnterProgressButton.setText("登录");
                            break;
                        default:
                            break;
                    }
                    Toast.makeText(LoginActivity.this,msg.getData().getString("message"),Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init() {
        mForgetTextView = (TextView) findViewById(R.id.tv_password);
        mForgetTextView.setOnClickListener(this);
        mRegisterTextView = (TextView) findViewById(R.id.tv_register);
        mRegisterTextView.setOnClickListener(this);
        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mBackImageView.setOnClickListener(this);
        mUserNameEditText = (EditText) findViewById(R.id.et_username);
        mUserNameEditText.setOnClickListener(this);
        mPasswordEditText = (EditText) findViewById(R.id.et_password);
        mPasswordEditText.setOnClickListener(this);
        mPasswordEditText.setOnEditorActionListener(this);
        mEnterProgressButton = (ProgressButton) findViewById(R.id.pb_enter);
        mEnterProgressButton.setOnClickListener(this);
        mDataBaseHelper = new DataBaseHelper(this,"Data.db",1);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.pb_enter:
                mEnterProgressButton.startRotate();
                mEnterProgressButton.setText("登录中");
                mUserNameEditText.setEnabled(false);
                mPasswordEditText.setEnabled(false);
                if (check()) {
                    login();
                } else {
                    mUserNameEditText.setEnabled(true);
                    mPasswordEditText.setEnabled(true);
                    mEnterProgressButton.animError();
                    mEnterProgressButton.setText("登录");
                }
                break;
            case R.id.tv_password:
                Log.d(TAG," check!@@");
                new AlertDialog.Builder(this)
                        .setMessage("请联系客服QQ549220297")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                finish();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                finish();
                            }
                        }).create().show();
                break;
            case R.id.tv_register:
                startRegisterActivity();
                break;
            default:
                break;
        }
    }

    private void login() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", mUserNameEditText.getText().toString());
                params.put("password", mPasswordEditText.getText().toString());
                String jsonString = HttpUtil.submitPostData(params, "utf-8", URLConfig.ACTION_LOGIN);
                if (jsonString.equals("")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("message","网络错误");
                    bundle.putInt("case",STATE_LOGIN);
                    Message message = new Message();
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                    return;
                }
                Log.d(TAG,"jsonString--"+jsonString);
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    if(jsonObject.isNull("code")){
                        UserObject userObject = new UserObject(jsonObject);
                        if(mDataBaseHelper.saveUser(userObject) == true){
                            mPreferences = getSharedPreferences("main",MODE_PRIVATE);
                            mEditor = mPreferences.edit();
                            mEditor.putBoolean(SharePreferencesConfig.ISLOGIN_BOOLEAN,true);
                            mEditor.putInt(SharePreferencesConfig.ID_INT,userObject.getUserId());
                            mEditor.putInt(SharePreferencesConfig.MONEY_INT,jsonObject.getInt("money"));
                            mEditor.putString(SharePreferencesConfig.USERNAME_STRING,userObject.getUsername());
                            mEditor.putString(SharePreferencesConfig.PASSWORD_STRING,mPasswordEditText.getText().toString());
                            mEditor.putString(SharePreferencesConfig.COOKIE_STRING,HttpUtil.submitPostData(params, SharePreferencesConfig.COOKIE_STRING, URLConfig.ACTION_LOGIN));
                            mEditor.commit();
                            finish();
                        }
                        Log.d(TAG,userObject.toString());
                    }else{
                        Log.d(TAG, "ERROR code:" + jsonObject.getInt("code") + ". message:" + jsonObject.getString("message"));
                        Bundle bundle = new Bundle();
                        bundle.putString("message",jsonObject.getString("message"));
                        bundle.putInt("case",STATE_LOGIN);
                        Message message = new Message();
                        message.setData(bundle);
                        mHandler.sendMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
    private void register() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", mUserNameEditText.getText().toString());
                params.put("password", mPasswordEditText.getText().toString());
                params.put("gender","1");
                params.put("address","顺德");
                params.put("phoneNumber","13111111111");
                params.put("signature","这个人比较懒，什么都没有留下");
                String jsonString = HttpUtil.submitPostData(params, "utf-8", URLConfig.ACTION_REGISTER);
                if (jsonString.equals("")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("message","网络错误");
                    bundle.putInt("case",STATE_REGISTER);
                    Message message = new Message();
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                    return;
                }
                Log.d(TAG,"jsonString--"+jsonString);
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    if (jsonObject.getInt("code") == 0){
                        login();
                    }else{
                        Bundle bundle = new Bundle();
                        bundle.putString("message",jsonObject.getString("message"));
                        bundle.putInt("case",STATE_REGISTER);
                        Message message = new Message();
                        message.setData(bundle);
                        mHandler.sendMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
    private boolean check() {
        if(mUserNameEditText.getText().equals("")) {
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(mUserNameEditText.getText().toString().length()>20) {
            Toast.makeText(this, "用户名长度不能超过20", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(mPasswordEditText.getText().toString().length() <6 || mPasswordEditText.getText().toString().length() > 16) {
            Toast.makeText(this, "密码长度需在6~16", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void startRegisterActivity(){
        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == KeyEvent.ACTION_DOWN || actionId == EditorInfo.IME_ACTION_DONE){
            mEnterProgressButton.startRotate();
            mEnterProgressButton.setText("登录中");
            mUserNameEditText.setEnabled(false);
            mPasswordEditText.setEnabled(false);
            if (check()) {
                login();
            } else {
                mUserNameEditText.setEnabled(true);
                mPasswordEditText.setEnabled(true);
                mEnterProgressButton.animError();
                mEnterProgressButton.setText("登录");
            }
        }
        return false;
    }
}
