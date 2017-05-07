package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
    private final static int GET_DETAILS = 3;
    private final static int GET_PHOTO = 4;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private TextView mMessageTextView;
    private TextView mUserNameTextView;
    private TextView mAreaTextView;
    private TextView mSignatureTextView;
    private TextView mPhoneTextView;
    private ImageView mMaleImageView;
    private CircleImageView mPhotoCircleImageView;
    private ImageView mBackImageView;
    private UserObject userObject;
    private DataBaseHelper mDataBaseHelper;
    private Bitmap bitmap = null;
    private boolean isFriends = true;
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
                case GET_DETAILS:
                    isFriends = false;
                    mMessageTextView.setVisibility(View.INVISIBLE);
                    mUserNameTextView.setText(userObject.getUsername());
                    mAreaTextView.setText(userObject.getaddress());
                    mSignatureTextView.setText(userObject.getSignature());
                    mPhoneTextView.setText(userObject.getPhoneNumber());
                    if (userObject.getGender() == 1) mMaleImageView.setImageResource(R.drawable.male);
                    else mMaleImageView.setImageResource(R.drawable.female);
                    break;
                case GET_PHOTO:
                    isFriends = false;
                    if (bitmap != null) mPhotoCircleImageView.setImageBitmap(bitmap);
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
        init();
        super.onResume();
    }

    private void init() {
        mPreferences = getSharedPreferences("main", MODE_PRIVATE);
        if (!mPreferences.getBoolean(SharePreferencesConfig.ISLOGIN_BOOLEAN, false)) {
            finish();
        }
        mDataBaseHelper = new DataBaseHelper(this, "Data.db", 1);
        userObject = mDataBaseHelper.getUserObjict(getIntent().getIntExtra("id", -1));
        if (userObject == null) {
            isFriends = false;
            userObject = new UserObject(getIntent().getIntExtra("id", -1),"非好友",1,"","","");
            getUserObject(userObject.getUserId());
            getUserPhoto(userObject.getUserId());
        }

        mMessageTextView = (TextView) findViewById(R.id.tv_message);
        mMessageTextView.setOnClickListener(this);

        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mBackImageView.setOnClickListener(this);
        mPhotoCircleImageView = (CircleImageView) findViewById(R.id.civ_photo);
        mPhotoCircleImageView.setOnClickListener(this);

        Bitmap bm = mDataBaseHelper.getUserPhotoToBitmap(userObject.getUserId());
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

        mMaleImageView = (ImageView) findViewById(R.id.iv_male);
        if (userObject.getGender() == 1) mMaleImageView.setImageResource(R.drawable.male);
        else mMaleImageView.setImageResource(R.drawable.female);
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
            case R.id.civ_photo:
                startPhotoActivity();
                break;
            default:
                break;
        }
    }

    private void getUserObject(final int id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", id + "");
                String result = HttpUtil.submitPostData(params, "utf-8", URLConfig.ACTION_SEARCH_FRIENDS_ID);
                Log.d(TAG, " GetUserMessage result;" + result);
                if (result == "") {
                    return;
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        userObject = new UserObject(jsonObject);
                        Bundle bundle = new Bundle();
                        bundle.putInt("case", GET_DETAILS);
                        Message message = new Message();
                        message.setData(bundle);
                        mHandler.sendMessage(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void getUserPhoto(final int id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                bitmap = HttpUtil.getPhpop(id);
                Log.d(TAG, " GetUserPhoto !");
                if (bitmap == null) {
                    return;
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putInt("case", GET_PHOTO);
                    Message message = new Message();
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }

    private void startChatActivity() {
        Intent intent = new Intent(FriendsDetailsActivity.this, ChatActivity.class);
        intent.putExtra("id", userObject.getUserId());
        startActivity(intent);
    }

    private void startPhotoActivity() {
        Intent intent = new Intent(FriendsDetailsActivity.this, PhotoActivity.class);
        if(isFriends) intent.putExtra("type", PhotoActivity.TYPE_USER_PHOTO);
        else intent.putExtra("type", PhotoActivity.TYPE_URL_PHOTO);
        intent.putExtra("id", userObject.getUserId());
        startActivity(intent);
    }

}
