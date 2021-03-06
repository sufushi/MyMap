package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rdc.mymap.R;

public class BicycleActivity extends Activity implements View.OnClickListener{

    //private MyMenu mMyMenu;
    private ImageView mBackImageView;
    private ImageView mCircleImageView;
    private TextView mTypeTextView;
    private TextView mPasswordTextView;
    private TextView mBtnTextView;
    private ImageView mCheckImageView;
    private TextView mResultTextView;
    private String mType;
    private int mState = 0;
    private static final int REQUEST_SCAN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bicycle);
        init();
    }

    private void init() {
        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mBackImageView.setOnClickListener(this);
        mCircleImageView = (ImageView) findViewById(R.id.civ_bicycle);
        mTypeTextView = (TextView) findViewById(R.id.tv_bicycle);
        mPasswordTextView = (TextView) findViewById(R.id.tv_ofo_password);
        mBtnTextView = (TextView) findViewById(R.id.tv_btn);
        mBtnTextView.setOnClickListener(this);
        mCheckImageView = (ImageView) findViewById(R.id.iv_check);
        mCheckImageView.setOnClickListener(this);
        mResultTextView = (TextView) findViewById(R.id.tv_result);
        String dataUrl = getIntent().getStringExtra("dataUrl");
        Log.e("error", dataUrl);
        if(dataUrl.contains("http://v2.yueqiquan.cn")) {
            mType = "小鸣单车";
        } else if(dataUrl.contains("https://www.bluegogo.com")) {
            mType = "小篮单车";
        } else if(dataUrl.contains("http://ofo.so")) {
            mType = "ofo单车";
        } else if(dataUrl.contains("http://www.mobike.com")) {
            mType = "摩拜单车";
        } else {
            mType = "未识别单车";
        }
        mTypeTextView.setText(mType);
        switch (mType) {
            case "小鸣单车" :
                mCircleImageView.setImageDrawable(getResources().getDrawable(R.drawable.mingbike));
                break;
            case "小篮单车" :
                mCircleImageView.setImageDrawable(getResources().getDrawable(R.drawable.bluegogo));
                break;
            case "ofo单车" :
                mCircleImageView.setImageDrawable(getResources().getDrawable(R.drawable.ofo));
                //mPasswordTextView.setVisibility(View.VISIBLE);
                mResultTextView.setText("解锁成功，密码2047");
                break;
            case "摩拜单车" :
                mCircleImageView.setImageDrawable(getResources().getDrawable(R.drawable.mobike));
                break;
            case "未识别单车" :
                mCircleImageView.setImageDrawable(getResources().getDrawable(R.drawable.unlock_bikes));
                mCheckImageView.setImageDrawable(getResources().getDrawable(R.drawable.error));
                mResultTextView.setText("解锁失败");
                mResultTextView.setTextColor(Color.parseColor("#d81e06"));
                mBtnTextView.setText("解锁");
                mState = 1;
                break;
            default:
                break;
        }

//        mMyMenu = (MyMenu) findViewById(R.id.my_menu);
//        mMyMenu.setOnMenuItemClickListener(new MyMenu.OnMenuItemClickListener() {
//            @Override
//            public void onClick(View view, int position) {
//                mMyMenu.setMainMenuBackground(position);
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_SCAN) {
            if(resultCode == RESULT_OK) {
                Intent bicycleIntent = new Intent(this, BicycleActivity.class);
                bicycleIntent.putExtra("dataUrl", data.getStringExtra("dataUrl"));
                startActivity(bicycleIntent);
                finish();
                //Toast.makeText(this, data.getStringExtra("dataUrl"), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "扫描失败", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back :
                finish();
                break;
            case R.id.iv_check :
                finish();
                break;
            case R.id.tv_btn :
                if(mState == 0) {
                    finish();
                } else {
                    Intent scanIntent = new Intent(this, CaptureActivity.class);
                    startActivityForResult(scanIntent, REQUEST_SCAN);
                }
            default:
                break;
        }
    }
}
