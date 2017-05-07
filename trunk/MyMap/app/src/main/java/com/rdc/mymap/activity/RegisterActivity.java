package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

public class RegisterActivity extends Activity implements View.OnClickListener, TextView.OnEditorActionListener {

    private final static String TAG = "RegisterActivity";
    private final static int STATE_NETWORK = 1;

    private final static int STATE_REGISTER = 1;
    private final static int OK = 3;


    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private ImageView mBackImageView;
    private EditText mUserNameEditText;
    private EditText mPasswordEditText;
    private EditText mPhoneNumberEditText;
    private EditText mAreaEditText;
    private EditText mSignEditText;
    private ProgressButton mEnterProgressButton;
    private DataBaseHelper mDataBaseHelper;
    private int i = 0;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case STATE_NETWORK:
                    mUserNameEditText.setEnabled(true);
                    mPasswordEditText.setEnabled(true);
                    mPhoneNumberEditText.setEnabled(true);
                    mAreaEditText.setEnabled(true);
                    mSignEditText.setEnabled(true);
                    mEnterProgressButton.animError();
                    mEnterProgressButton.setText("注册");
                    Toast.makeText(RegisterActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    mUserNameEditText.setEnabled(true);
                    mPasswordEditText.setEnabled(true);
                    mPhoneNumberEditText.setEnabled(true);
                    mAreaEditText.setEnabled(true);
                    mSignEditText.setEnabled(true);
                    switch (msg.getData().getInt("case")) {
                        case STATE_REGISTER:
                            mEnterProgressButton.animError();
                            mEnterProgressButton.setText("注册");
                            break;
                        case OK:
                            Toast.makeText(RegisterActivity.this, "注册成功！请登录。", Toast.LENGTH_SHORT).show();
                            finish();
                            break;
                        default:
                            break;
                    }
                    Toast.makeText(RegisterActivity.this, msg.getData().getString("message"), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);
        init();
    }

    private void init() {
        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mBackImageView.setOnClickListener(this);
        mUserNameEditText = (EditText) findViewById(R.id.et_username);
        mPasswordEditText = (EditText) findViewById(R.id.et_password);
        mPhoneNumberEditText = (EditText) findViewById(R.id.et_phonenumber);
        mAreaEditText = (EditText) findViewById(R.id.et_area);
        mSignEditText = (EditText) findViewById(R.id.et_sign);

        mSignEditText.setOnEditorActionListener(this);
        mEnterProgressButton = (ProgressButton) findViewById(R.id.pb_enter);
        mEnterProgressButton.setOnClickListener(this);
        mDataBaseHelper = new DataBaseHelper(this, "Data.db", 1);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.pb_enter:
                if (check()) {
                    mEnterProgressButton.startRotate();
                    mEnterProgressButton.setText("注册中");
                    mUserNameEditText.setEnabled(false);
                    mPasswordEditText.setEnabled(false);
                    mPhoneNumberEditText.setEnabled(false);
                    mAreaEditText.setEnabled(false);
                    mSignEditText.setEnabled(false);
                    register();
                } else {
                    mUserNameEditText.setEnabled(true);
                    mPasswordEditText.setEnabled(true);
                    mPhoneNumberEditText.setEnabled(true);
                    mAreaEditText.setEnabled(true);
                    mSignEditText.setEnabled(true);
                    mEnterProgressButton.animError();
                    mEnterProgressButton.setText("注册");
                }
                break;
            default:
                break;
        }
    }

    private void register() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", mUserNameEditText.getText().toString());
                params.put("password", mPasswordEditText.getText().toString());
                params.put("gender", "1");
                params.put("address", mAreaEditText.getText().toString());
                params.put("phoneNumber", mPhoneNumberEditText.getText().toString());
                params.put("signature", mSignEditText.getText().toString());
                String jsonString = HttpUtil.submitPostData(params, "utf-8", URLConfig.ACTION_REGISTER);
                if (jsonString.equals("")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("message", "网络错误");
                    bundle.putInt("case", STATE_REGISTER);
                    Message message = new Message();
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                    return;
                }
                Log.d(TAG, "jsonString--" + jsonString);
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    if (jsonObject.getInt("code") == 0) {
                        Bundle bundle = new Bundle();
                        bundle.putString("message", jsonObject.getString("message"));
                        bundle.putInt("case", OK);
                        Message message = new Message();
                        message.setData(bundle);
                        mHandler.sendMessage(message);
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString("message", jsonObject.getString("message"));
                        bundle.putInt("case", STATE_REGISTER);
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
        if (mUserNameEditText.getText().equals("")) {
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mUserNameEditText.getText().toString().length() > 20) {
            Toast.makeText(this, "用户名长度不能超过20", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mPasswordEditText.getText().toString().length() < 6 || mPasswordEditText.getText().toString().length() > 16) {
            Toast.makeText(this, "密码长度需在6~16", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void startDetailsActivity() {
        Intent intent = new Intent(RegisterActivity.this, DetailsActivity.class);
        startActivity(intent);
        finish();
    }

    private void startRegisterActivity() {
        Intent intent = new Intent(RegisterActivity.this, PersonCenterActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == KeyEvent.ACTION_DOWN || actionId == EditorInfo.IME_ACTION_DONE) {
            mEnterProgressButton.startRotate();
            mEnterProgressButton.setText("注册中");
            mUserNameEditText.setEnabled(false);
            mPasswordEditText.setEnabled(false);
            mPhoneNumberEditText.setEnabled(false);
            mAreaEditText.setEnabled(false);
            mSignEditText.setEnabled(false);
            if (check()) {
                register();
            } else {
                mUserNameEditText.setEnabled(true);
                mPasswordEditText.setEnabled(true);
                mPhoneNumberEditText.setEnabled(true);
                mAreaEditText.setEnabled(true);
                mSignEditText.setEnabled(true);
                mEnterProgressButton.animError();
                mEnterProgressButton.setText("注册");
            }
        }
        return false;
    }
}
