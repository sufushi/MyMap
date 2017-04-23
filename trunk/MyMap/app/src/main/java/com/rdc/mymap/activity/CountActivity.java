package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.rdc.mymap.R;

import cn.xm.weidongjian.progressbuttonlib.ProgressButton;

/**
 * Created by wsoyz on 2017/4/22.
 */

public class CountActivity extends Activity implements TextWatcher, View.OnClickListener {
    private final static String TAG = "CountActivity";
    private TextView mTitleTextView;
    private EditText mMoneyEditText;
    private ImageView mBackImageView;
    private ProgressButton mEnterProgressButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_count);
        init();
    }
    private void init(){
        mTitleTextView = (TextView) findViewById(R.id.tv_title);
        mMoneyEditText = (EditText) findViewById(R.id.et_money);
        mMoneyEditText.addTextChangedListener(this);
        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mBackImageView.setOnClickListener(this);
        mEnterProgressButton = (ProgressButton) findViewById(R.id.pb_enter);
        mEnterProgressButton.setOnClickListener(this);
    }
    @Override
    protected void onResume() {
        init();
        super.onResume();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        Log.d(TAG," (before)money Text;"+mMoneyEditText.getText().toString());
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        Log.d(TAG," (on)money Text;"+mMoneyEditText.getText().toString());
    }

    @Override
    public void afterTextChanged(Editable edt) {
        String temp = edt.toString();
        int posDot = temp.indexOf(".");
        if (posDot <= 0) return;
        if (temp.length() - posDot - 1 > 2)
        {
            edt.delete(posDot + 3, posDot + 4);
        }
        Log.d(TAG," (after)money Text;"+mMoneyEditText.getText().toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.pb_enter:
                startPayActivity();
                break;
            default:
                break;
        }
    }
    private void startPayActivity() {
        Intent intent = new Intent(CountActivity.this,PayActivity.class);
        intent.putExtra("name","转入");
        intent.putExtra("money",(int)(Double.parseDouble(mMoneyEditText.getText().toString())*100));
        startActivity(intent);
        finish();
    }
}
