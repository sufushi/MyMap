package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
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

/**
 * Created by wsoyz on 2017/4/19.
 */

public class WalletActivity extends Activity implements View.OnClickListener {
    private final static String TAG = "WalletActivity";
    private ImageView mBackImageView;
    private ImageView mPlusBankImageView;
    private TextView mMoneyTextView;
    private TextView mInTextView;
    private TextView mOoutTextView;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private DataBaseHelper mDataBaseHelper;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
//                case STATE_NETWORK :
//                    Toast.makeText(WalletActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
//                    break;
                default:
                    Toast.makeText(WalletActivity.this, msg.getData().getString("message"), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_wallet);
        init();
    }


    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        mDataBaseHelper = new DataBaseHelper(this, "Data.db", 1);
        mPreferences = getSharedPreferences("main", MODE_PRIVATE);
        mEditor = mPreferences.edit();
        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mBackImageView.setOnClickListener(this);
        mPlusBankImageView = (ImageView) findViewById(R.id.iv_plus);
        mPlusBankImageView.setOnClickListener(this);
        mInTextView = (TextView) findViewById(R.id.tv_in);
        mInTextView.setOnClickListener(this);
        mOoutTextView = (TextView) findViewById(R.id.tv_out);
        mOoutTextView.setOnClickListener(this);
        mMoneyTextView = (TextView) findViewById(R.id.tv_money);
        refresh();
        mMoneyTextView.setText(String.format("%.2f", mPreferences.getInt(SharePreferencesConfig.MONEY_INT, -1)*0.01));
    }

    private void refresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", mPreferences.getString(SharePreferencesConfig.USERNAME_STRING, ""));
                params.put("password", mPreferences.getString(SharePreferencesConfig.PASSWORD_STRING, ""));
                String jsonString = HttpUtil.submitPostData(params, "utf-8", URLConfig.ACTION_LOGIN);
                if (jsonString.equals("")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("message", "网络错误");
                    Message message = new Message();
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                    return;
                }
                Log.d(TAG, "jsonString--" + jsonString);
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    if (jsonObject.isNull("code")) {
                        UserObject userObject = new UserObject(jsonObject);
                        mPreferences = getSharedPreferences("main", MODE_PRIVATE);
                        mEditor = mPreferences.edit();
                        mEditor.putBoolean(SharePreferencesConfig.ISLOGIN_BOOLEAN, true);
                        mEditor.putInt(SharePreferencesConfig.ID_INT, userObject.getUserId());
                        mEditor.putInt(SharePreferencesConfig.MONEY_INT, jsonObject.getInt("money"));
                        mEditor.putString(SharePreferencesConfig.USERNAME_STRING, userObject.getUsername());
                        mEditor.putString(SharePreferencesConfig.COOKIE_STRING, HttpUtil.submitPostData(params, SharePreferencesConfig.COOKIE_STRING, URLConfig.ACTION_LOGIN));
                        mEditor.commit();
                        Log.d(TAG, userObject.toString());
                    } else {
                        Log.d(TAG, "ERROR code:" + jsonObject.getInt("code") + ". message:" + jsonObject.getString("message"));
                        Bundle bundle = new Bundle();
                        bundle.putString("message", jsonObject.getString("message"));
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_plus:
                Toast.makeText(this,"暂不能绑定",Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_in:
                startCountActivity();
                break;
            case R.id.tv_out:
                Toast.makeText(this,"暂不能转出",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
    private void startCountActivity(){
        Intent intent = new Intent(WalletActivity.this,CountActivity.class);
        intent.putExtra("name","转入");
        startActivity(intent);
    }
    private void startPayActivity(){
        Intent intent = new Intent(WalletActivity.this,PayActivity.class);
        startActivity(intent);
    }
}
