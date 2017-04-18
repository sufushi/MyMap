package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rdc.mymap.R;
import com.rdc.mymap.config.SharePreferencesConfig;
import com.rdc.mymap.database.DataBaseHelper;
import com.rdc.mymap.view.CircleImageView;

public class PersonCenterActivity extends Activity implements View.OnClickListener{

    private static final String TAG = "PersonCenterActivity";
    private ImageView mBackImageView;
    private CircleImageView mPhotoCircleImageView;
    private TextView mLoginTextView;
    private TextView mUsernameTextView;
    private DataBaseHelper mDataBaseHelper;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;



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
        mUsernameTextView = (TextView) findViewById(R.id.tv_username);
        mLoginTextView.setOnClickListener(this);
        mDataBaseHelper = new DataBaseHelper(this,"Data.db",1);
        isLogin();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back :
                finish();
                break;
            case R.id.civ_photo:
                if(mPreferences.getBoolean(SharePreferencesConfig.ISLOGIN_BOOLEAN,false)){
                    startDetailsActivity();
                }else{
                    Toast.makeText(this,"请先登录",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_login:
                if(mPreferences.getBoolean(SharePreferencesConfig.ISLOGIN_BOOLEAN,false)){
                    LoginOut();
                    mPhotoCircleImageView.setImageResource(R.drawable.pikaqiu);
                    mLoginTextView.setText("立即登录");
                    mUsernameTextView.setText("皮皮虾");
                }else{
                    startLoginActivity();
                }
                break;
            default:
                break;
        }
    }
    private void startLoginActivity(){
        Intent intent = new Intent(PersonCenterActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
    private void startDetailsActivity(){
        Intent intent = new Intent(PersonCenterActivity.this,DetailsActivity.class);
        startActivity(intent);
        finish();
    }
    private void isLogin(){
        mPreferences = getSharedPreferences("main",MODE_PRIVATE);
        mEditor = mPreferences.edit();
        if(mPreferences.getBoolean(SharePreferencesConfig.ISLOGIN_BOOLEAN,false)){
            Log.d(TAG,"Logined!");
            mLoginTextView.setText("登出");
            Bitmap bm = mDataBaseHelper.getPhoto(mPreferences.getInt(SharePreferencesConfig.ID_INT,-1));
            mUsernameTextView.setText(mPreferences.getString(SharePreferencesConfig.USERNAME_STRING,""));
            if (bm != null)mPhotoCircleImageView.setImageBitmap(bm);
        }else{
            Log.d(TAG,"NotLogin!");
        }
    }
    private void LoginOut(){
        mEditor = mPreferences.edit();
        mEditor.clear();
        mEditor.commit();
        mDataBaseHelper.clear();
    }
}
