package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rdc.mymap.R;

import java.util.Timer;
import java.util.TimerTask;

import cn.xm.weidongjian.progressbuttonlib.ProgressButton;

/**
 * Created by wsoyz on 2017/4/22.
 */

public class CountActivity extends Activity implements TextWatcher, View.OnClickListener, TextView.OnEditorActionListener {
    public final static int IN = 1;
    public final static int OUT = -1;
    private final static String TAG = "CountActivity";
    private TextView mTitleTextView;
    private TextView mWayTextView;
    private EditText mMoneyEditText;
    private ImageView mBackImageView;
    private ProgressButton mEnterProgressButton;
    private int way = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_count);
        init();
    }
    private void init(){
        mTitleTextView = (TextView) findViewById(R.id.tv_title);
        mWayTextView = (TextView) findViewById(R.id.tv_way);
        Intent intent = getIntent();
        mTitleTextView.setText(intent.getStringExtra("name"));
        switch (intent.getIntExtra("way",0)){
            case IN:
                mWayTextView.setText("支付方式");
                way = IN;
                break;
            case OUT:
                mWayTextView.setText("转出到");
                way = OUT;
                break;
            default:
                finish();
                break;
        }
        mMoneyEditText = (EditText) findViewById(R.id.et_money);
        mMoneyEditText.addTextChangedListener(this);
        mMoneyEditText.setOnEditorActionListener(this);
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
        switch (way){
            case IN:
                intent.putExtra("name","转入");
                break;
            case OUT:
                intent.putExtra("name","转出");
                break;
            default:
                finish();
                break;
        }
        if(mMoneyEditText.getText().toString().length() == 0) {
            Toast.makeText(this,"请输入金额",Toast.LENGTH_SHORT).show();
            return ;
        }else{
            intent.putExtra("money",(int)(Double.parseDouble(mMoneyEditText.getText().toString())*100)*way);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == KeyEvent.ACTION_DOWN || actionId == EditorInfo.IME_ACTION_DONE){
            startPayActivity();
        }
        return false;
    }
}
