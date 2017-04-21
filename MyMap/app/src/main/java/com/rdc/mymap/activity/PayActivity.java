package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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

import com.rdc.mymap.R;

/**
 * Created by wsoyz on 2017/4/20.
 */

public class PayActivity extends Activity implements View.OnClickListener, TextWatcher {
    private final static String TAG = "PayActivity";
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
    private LinearLayout mPasswordLinerLayout1;
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
        init();
    }
    private void init(){
        mItemTextView = (TextView) findViewById(R.id.tv_item);
        mMoneyTextView = (TextView) findViewById(R.id.tv_money);
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
        if(mPasswordEditText.getText().toString().equals("666666")) Log.d(TAG,"OK!");
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
