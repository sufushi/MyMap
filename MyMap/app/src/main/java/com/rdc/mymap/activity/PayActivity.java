package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rdc.mymap.R;
import com.rdc.mymap.config.SharePreferencesConfig;
import com.rdc.mymap.config.URLConfig;
import com.rdc.mymap.utils.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wsoyz on 2017/4/20.
 */

public class PayActivity extends Activity implements View.OnClickListener, TextWatcher {
    private final static String TAG = "PayActivity";
    private final static int OK = 1;
    private final static int NOTOK = 0;
    private TextView mItemTextView;
    private TextView mMoneyTextView;
    private ImageView mDotIamgeView1;
    private ImageView mDotIamgeView2;
    private ImageView mDotIamgeView3;
    private ImageView mDotIamgeView4;
    private ImageView mDotIamgeView5;
    private ImageView mDotIamgeView6;
    private EditText mPasswordEditText;
    private LinearLayout mPasswordLinerLayout;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.getData().getInt("case")){
                case OK:
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mPasswordEditText.getWindowToken(),0);
                    Toast.makeText(PayActivity.this,"支付成功",Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case NOTOK:
                    Toast.makeText(PayActivity.this, msg.getData().getString("message"), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(PayActivity.this, msg.getData().getString("message"), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pay_dialog);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    private void init(){
        Intent intent = getIntent();
        mItemTextView = (TextView) findViewById(R.id.tv_item);
        mItemTextView.setText(intent.getStringExtra("name"));
        mMoneyTextView = (TextView) findViewById(R.id.tv_money);
        mMoneyTextView.setText(intent.getIntExtra("money",-1)*0.01+"");
        mDotIamgeView1 = (ImageView) findViewById(R.id.iv_dot1);
        mDotIamgeView2 = (ImageView) findViewById(R.id.iv_dot2);
        mDotIamgeView3 = (ImageView) findViewById(R.id.iv_dot3);
        mDotIamgeView4 = (ImageView) findViewById(R.id.iv_dot4);
        mDotIamgeView5 = (ImageView) findViewById(R.id.iv_dot5);
        mDotIamgeView6 = (ImageView) findViewById(R.id.iv_dot6);
        mDotIamgeView1.setVisibility(View.INVISIBLE);
        mDotIamgeView2.setVisibility(View.INVISIBLE);
        mDotIamgeView3.setVisibility(View.INVISIBLE);
        mDotIamgeView4.setVisibility(View.INVISIBLE);
        mDotIamgeView5.setVisibility(View.INVISIBLE);
        mDotIamgeView6.setVisibility(View.INVISIBLE);
        mPasswordLinerLayout = (LinearLayout) findViewById(R.id.ll_password);
        mPasswordLinerLayout.setOnClickListener(this);
        mPasswordEditText = (EditText) findViewById(R.id.et_password);
        mPasswordEditText.addTextChangedListener(this);
    }
    private void check(){
        if(mPasswordEditText.getText().toString().equals("666666")) {
            Log.d(TAG,"OK!");
            Intent intent = getIntent();
            final int money = intent.getIntExtra("money",-1);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mSharedPreferences = getSharedPreferences("main",MODE_PRIVATE);
                    mEditor = mSharedPreferences.edit();
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("money",money+"");
                    String result = HttpUtil.submitPostDataWithCookie(params,mSharedPreferences.getString(SharePreferencesConfig.COOKIE_STRING,""), URLConfig.ACTION_CHARGE);
                    Log.d(TAG," return:"+result);
                    if(result.equals("")){
                        Bundle bundle = new Bundle();
                        bundle.putString("message","网络错误");
                        bundle.putInt("case",NOTOK);
                        Message message = new Message();
                        message.setData(bundle);
                        mHandler.sendMessage(message);
                    }
                    if(result.startsWith("{")){
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            Bundle bundle = new Bundle();
                            bundle.putString("message",jsonObject.getString("message"));
                            bundle.putInt("case",NOTOK);
                            Message message = new Message();
                            message.setData(bundle);
                            mHandler.sendMessage(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else{
                        mEditor.putInt(SharePreferencesConfig.MONEY_INT,mSharedPreferences.getInt(SharePreferencesConfig.MONEY_INT,0)+money);
                        mEditor.commit();
                        Bundle bundle = new Bundle();
                        bundle.putInt("case",OK);
                        Message message = new Message();
                        message.setData(bundle);
                        mHandler.sendMessage(message);
                    }
                }
            }).start();
        }else{
            Toast.makeText(this,"密码错误",Toast.LENGTH_SHORT).show();
            mPasswordEditText.setText("");
            mDotIamgeView1.setVisibility(View.INVISIBLE);
            mDotIamgeView2.setVisibility(View.INVISIBLE);
            mDotIamgeView3.setVisibility(View.INVISIBLE);
            mDotIamgeView4.setVisibility(View.INVISIBLE);
            mDotIamgeView5.setVisibility(View.INVISIBLE);
            mDotIamgeView6.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_password:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mPasswordEditText, InputMethodManager.SHOW_FORCED);
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        Log.d(TAG," (after)money Text;"+mPasswordEditText.getText().toString());
        switch (mPasswordEditText.getText().toString().length()){
            case 6:
                mDotIamgeView6.setVisibility(View.VISIBLE);
                mDotIamgeView5.setVisibility(View.VISIBLE);
                mDotIamgeView4.setVisibility(View.VISIBLE);
                mDotIamgeView3.setVisibility(View.VISIBLE);
                mDotIamgeView2.setVisibility(View.VISIBLE);
                mDotIamgeView1.setVisibility(View.VISIBLE);
                check();
                break;
            case 5:
                mDotIamgeView6.setVisibility(View.INVISIBLE);
                mDotIamgeView5.setVisibility(View.VISIBLE);
                mDotIamgeView4.setVisibility(View.VISIBLE);
                mDotIamgeView3.setVisibility(View.VISIBLE);
                mDotIamgeView2.setVisibility(View.VISIBLE);
                mDotIamgeView1.setVisibility(View.VISIBLE);
                break;
            case 4:
                mDotIamgeView5.setVisibility(View.INVISIBLE);
                mDotIamgeView6.setVisibility(View.INVISIBLE);
                mDotIamgeView4.setVisibility(View.VISIBLE);
                mDotIamgeView3.setVisibility(View.VISIBLE);
                mDotIamgeView2.setVisibility(View.VISIBLE);
                mDotIamgeView1.setVisibility(View.VISIBLE);
                break;
            case 3:
                mDotIamgeView4.setVisibility(View.INVISIBLE);
                mDotIamgeView5.setVisibility(View.INVISIBLE);
                mDotIamgeView6.setVisibility(View.INVISIBLE);
                mDotIamgeView3.setVisibility(View.VISIBLE);
                break;
            case 2:
                mDotIamgeView3.setVisibility(View.INVISIBLE);
                mDotIamgeView4.setVisibility(View.INVISIBLE);
                mDotIamgeView5.setVisibility(View.INVISIBLE);
                mDotIamgeView6.setVisibility(View.INVISIBLE);
                mDotIamgeView2.setVisibility(View.VISIBLE);
                mDotIamgeView1.setVisibility(View.VISIBLE);
                break;
            case 1:
                mDotIamgeView2.setVisibility(View.INVISIBLE);
                mDotIamgeView3.setVisibility(View.INVISIBLE);
                mDotIamgeView4.setVisibility(View.INVISIBLE);
                mDotIamgeView5.setVisibility(View.INVISIBLE);
                mDotIamgeView6.setVisibility(View.INVISIBLE);
                mDotIamgeView1.setVisibility(View.VISIBLE);
                break;
            case 0:
                mDotIamgeView1.setVisibility(View.INVISIBLE);
                mDotIamgeView2.setVisibility(View.INVISIBLE);
                mDotIamgeView3.setVisibility(View.INVISIBLE);
                mDotIamgeView4.setVisibility(View.INVISIBLE);
                mDotIamgeView5.setVisibility(View.INVISIBLE);
                mDotIamgeView6.setVisibility(View.INVISIBLE);
                break;
        }
    }
}
