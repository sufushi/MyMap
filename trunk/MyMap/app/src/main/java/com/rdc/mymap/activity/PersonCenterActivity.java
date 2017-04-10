package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.rdc.mymap.R;
import com.rdc.mymap.view.CircleImageView;

public class PersonCenterActivity extends Activity implements View.OnClickListener{

    private ImageView mBackImageView;
    private CircleImageView mPhotoCircleImageView;
    private TextView mLoginTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_person_center);
        init();
    }

    private void init() {
        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mBackImageView.setOnClickListener(this);
        mPhotoCircleImageView = (CircleImageView) findViewById(R.id.civ_photo);
        mPhotoCircleImageView.setOnClickListener(this);
        mLoginTextView = (TextView) findViewById(R.id.tv_login);
        mLoginTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back :
                finish();
                break;
            case R.id.civ_photo:
                startDetailsActivity();
                break;
            case R.id.tv_login:
                startLoginActivity();
                break;
            default:
                break;
        }
    }
    private void startLoginActivity(){
        Intent intent = new Intent(PersonCenterActivity.this,LoginActivity.class);
        startActivity(intent);
    }
    private void startDetailsActivity(){
        Intent intent = new Intent(PersonCenterActivity.this,DetailsActivity.class);
        startActivity(intent);
    }
}
