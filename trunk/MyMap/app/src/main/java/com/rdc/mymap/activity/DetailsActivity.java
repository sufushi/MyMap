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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

import cn.xm.weidongjian.progressbuttonlib.ProgressButton;

/**
 * Created by wsoyz on 2017/4/8.
 */

public class DetailsActivity extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private static final String TAG = "DetailsActivity";
    private static final int OK = 1;
    private static final int NOTOK = 2;

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private TextView mChangeTextView;
    private EditText mUserNameEditText;
    private EditText mAreaEditText;
    private EditText mSignatureEditText;
    private EditText mPhoneEditText;
    private Spinner mMaleSpinner;
    private CircleImageView mPhotoCircleImageView;
    private ImageView mBackImageView;
    private UserObject userObject;
    private DataBaseHelper mDataBaseHelper;
    private int male = 0;
    private int flag = 0;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.getData().getInt("case", 0)) {
                case OK:
                    flag = 0;
                    mChangeTextView.setText("编辑");
                    Toast.makeText(DetailsActivity.this, msg.getData().getString("message"), Toast.LENGTH_SHORT).show();
                    break;
                case NOTOK:
                    break;
                default:
                    Toast.makeText(DetailsActivity.this, msg.getData().getString("message"), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);
        init();
    }

    @Override
    protected void onResume() {
        Bitmap bm = mDataBaseHelper.getPhotoToBitmap(mPreferences.getInt(SharePreferencesConfig.ID_INT, 0));
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
        userObject = mDataBaseHelper.getUserObjict(mPreferences.getInt(SharePreferencesConfig.ID_INT, 0));

        mChangeTextView = (TextView) findViewById(R.id.tv_change);
        mChangeTextView.setOnClickListener(this);

        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mBackImageView.setOnClickListener(this);
        mPhotoCircleImageView = (CircleImageView) findViewById(R.id.civ_photo);
        mPhotoCircleImageView.setOnClickListener(this);
        Bitmap bm = mDataBaseHelper.getPhotoToBitmap(mPreferences.getInt(SharePreferencesConfig.ID_INT, 0));
        if (bm != null) mPhotoCircleImageView.setImageBitmap(bm);
        else mPhotoCircleImageView.setImageResource(R.drawable.pikaqiu);

        mUserNameEditText = (EditText) findViewById(R.id.et_username);
        mUserNameEditText.setText(userObject.getUsername());
        mUserNameEditText.setEnabled(false);

        mAreaEditText = (EditText) findViewById(R.id.et_area);
        mAreaEditText.setText(userObject.getaddress());
        mAreaEditText.setEnabled(false);

        mSignatureEditText = (EditText) findViewById(R.id.et_sign);
        mSignatureEditText.setText(userObject.getSignature());
        mSignatureEditText.setEnabled(false);

        mPhoneEditText = (EditText) findViewById(R.id.et_phonenumber);
        mPhoneEditText.setText(userObject.getPhoneNumber());
        mPhoneEditText.setEnabled(false);

        mMaleSpinner = (Spinner) findViewById(R.id.s_male);
        mMaleSpinner.setOnItemSelectedListener(this);
        mMaleSpinner.setEnabled(false);

        if (userObject.getGender() == 1) {
            mMaleSpinner.setSelection(1);
        } else {
            mMaleSpinner.setSelection(0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.civ_photo:
                if (flag != 0) {
                    startPhotoDialogActivity();
                }
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_change:
                if (flag == 0) {
                    flag = 1;
                    mMaleSpinner.setEnabled(true);
                    mPhoneEditText.setEnabled(true);
                    mSignatureEditText.setEnabled(true);
                    mAreaEditText.setEnabled(true);
                    mChangeTextView.setText("提交");
                }else if (flag == 1) {
                    mMaleSpinner.setEnabled(false);
                    mPhoneEditText.setEnabled(false);
                    mSignatureEditText.setEnabled(false);
                    mAreaEditText.setEnabled(false);
                    change();
                } else {
                    mMaleSpinner.setEnabled(false);
                    mPhoneEditText.setEnabled(false);
                    mSignatureEditText.setEnabled(false);
                    mAreaEditText.setEnabled(false);
                }
                break;
            default:
                break;
        }
    }

    private void change() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences mPreferences = getSharedPreferences("main", MODE_PRIVATE);
                Map<String, String> params = new HashMap<String, String>();
                mDataBaseHelper = new DataBaseHelper(DetailsActivity.this, "Data.db", 1);
                UserObject userObject = mDataBaseHelper.getUserObjict(mPreferences.getInt(SharePreferencesConfig.ID_INT, -1));
                params.put("gender", male + "");
                userObject.setGender(male);
                params.put("address", mAreaEditText.getText().toString());
                userObject.setaddress(mAreaEditText.getText().toString());
                params.put("signature", mSignatureEditText.getText().toString());
                userObject.setSignature(mSignatureEditText.getText().toString());
                params.put("phoneNumber", mPhoneEditText.getText().toString());
                userObject.setPhoneNumber(mPhoneEditText.getText().toString());
                String jsonString = HttpUtil.submitPostDataWithCookie(params, mPreferences.getString(SharePreferencesConfig.COOKIE_STRING, ""), URLConfig.ACTION_CHANGE);
                if (jsonString.equals("")) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("case", NOTOK);
                    bundle.putString("message", "网络错误");
                    Message message = new Message();
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    if (jsonObject.isNull("code")) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("case", NOTOK);
                        bundle.putString("message", jsonObject.getString("错误"));
                        Message message = new Message();
                        message.setData(bundle);
                        mHandler.sendMessage(message);
                    } else {
                        if (jsonObject.getInt("code") == 0) {
                            Log.d(TAG, "ERROR code:" + jsonObject.getInt("code") + ". message:" + jsonObject.getString("message"));
                            Bundle bundle = new Bundle();
                            bundle.putInt("case", OK);
                            bundle.putString("message", "成功修改！");
                            Message message = new Message();
                            message.setData(bundle);
                            mHandler.sendMessage(message);
                            mDataBaseHelper.saveUser(userObject);
                        } else {
                            Log.d(TAG, "ERROR code:" + jsonObject.getInt("code") + ". message:" + jsonObject.getString("message"));
                            Bundle bundle = new Bundle();
                            bundle.putString("message", jsonObject.getString("message"));
                            Message message = new Message();
                            message.setData(bundle);
                            mHandler.sendMessage(message);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void startPersonCenterActivity() {
        Intent intent = new Intent(DetailsActivity.this, PersonCenterActivity.class);
        startActivity(intent);
    }

    private void startPhotoDialogActivity() {
        Intent intent = new Intent(DetailsActivity.this, PhotoDialogActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        male = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
