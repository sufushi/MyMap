package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.rdc.mymap.R;
import com.rdc.mymap.config.SharePreferencesConfig;
import com.rdc.mymap.config.URLConfig;
import com.rdc.mymap.database.DataBaseHelper;
import com.rdc.mymap.model.UserObject;
import com.rdc.mymap.utils.HttpUtil;
import com.rdc.mymap.view.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wsoyz on 2017/4/8.
 */

public class FriendsDetailsActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "DetailsActivity";
    private static final int OK = 1;
    private static final int NOTOK = 2;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    
    private TextView mMessageTextView;
    private TextView mUserNameTextView;
    private TextView mAreaTextView;
    private TextView mSignatureTextView;
    private TextView mPhoneTextView;
    private TextView mMaleTextView;
    private CircleImageView mPhotoCircleImageView;
    private ImageView mBackImageView;
    private UserObject userObject;
    private DataBaseHelper mDataBaseHelper;
    private boolean male = true;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.getData().getInt("case", 0)) {
                case OK:
                    Toast.makeText(FriendsDetailsActivity.this, msg.getData().getString("message"), Toast.LENGTH_SHORT).show();
                    break;
                case NOTOK:
                    break;
                default:
                    Toast.makeText(FriendsDetailsActivity.this, msg.getData().getString("message"), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_friends_details);
        init();
    }

    @Override
    protected void onResume() {
        Bitmap bm = mDataBaseHelper.getPhotoToBitmap(userObject.getUserId());
        if (bm != null) mPhotoCircleImageView.setImageBitmap(bm);
        else mPhotoCircleImageView.setImageResource(R.drawable.pikaqiu);
        super.onResume();
    }

    private void init() {
        mPreferences = getSharedPreferences("main", MODE_PRIVATE);
        if (!mPreferences.getBoolean(SharePreferencesConfig.ISLOGIN_BOOLEAN, false)) {
            finish();
        }
        mDataBaseHelper = new DataBaseHelper(this, "Data.db", 1);
        userObject = mDataBaseHelper.getUserObjict(getIntent().getIntExtra("id",-1));
        if(userObject == null) finish();
        mMessageTextView = (TextView) findViewById(R.id.tv_message);
        mMessageTextView.setOnClickListener(this);

        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mBackImageView.setOnClickListener(this);
        mPhotoCircleImageView = (CircleImageView) findViewById(R.id.civ_photo);
        mPhotoCircleImageView.setOnClickListener(this);

        Bitmap bm = mDataBaseHelper.getPhotoToBitmap(mPreferences.getInt(SharePreferencesConfig.ID_INT, 0));
        if (bm != null) mPhotoCircleImageView.setImageBitmap(bm);
        else mPhotoCircleImageView.setImageResource(R.drawable.pikaqiu);

        mUserNameTextView = (TextView) findViewById(R.id.tv_username);
        mUserNameTextView.setText(userObject.getUsername());

        mAreaTextView = (TextView) findViewById(R.id.tv_area);
        mAreaTextView.setText(userObject.getaddress());

        mSignatureTextView = (TextView) findViewById(R.id.tv_sign);
        mSignatureTextView.setText(userObject.getSignature());

        mPhoneTextView = (TextView) findViewById(R.id.tv_phonenumber);
        mPhoneTextView.setText(userObject.getPhoneNumber());

        mMaleTextView = (TextView) findViewById(R.id.tv_male);
        if(userObject.getGender() == 1)mMaleTextView.setText("男");
        else mMaleTextView.setText("女");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_message:
                startChatActivity();
                break;
            case R.id.iv_back:
                finish();
                break;
            default:
                break;
        }
    }

    private void startChatActivity() {
        Intent intent = new Intent(FriendsDetailsActivity.this, ChatActivity.class);
        intent.putExtra("id",userObject.getUserId());
        startActivity(intent);
    }

}
