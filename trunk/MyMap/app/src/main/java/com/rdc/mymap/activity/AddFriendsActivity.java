package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rdc.mymap.R;
import com.rdc.mymap.adapter.FriendsListAdapter;
import com.rdc.mymap.config.SharePreferencesConfig;
import com.rdc.mymap.config.URLConfig;
import com.rdc.mymap.database.DataBaseHelper;
import com.rdc.mymap.model.FriendsListItem;
import com.rdc.mymap.model.FriendsListSortComparator;
import com.rdc.mymap.model.UserObject;
import com.rdc.mymap.utils.HttpUtil;
import com.rdc.mymap.view.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import cn.xm.weidongjian.progressbuttonlib.ProgressButton;

/**
 * Created by wsoyz on 2017/4/24.
 */

public class AddFriendsActivity extends Activity implements View.OnClickListener, TextWatcher {
    private final static String TAG = "AddFriendsActivity";
    private final static int OK = 1;
    private final static int NOTOK = 2;
    private final static int PHOTO = 3;
    private int id = -1;
    private ImageView mBackImageView;
    private EditText mSearchView;
    private EditText mUserNameEditText;
    private EditText mAreaEditText;
    private EditText mSignatureEditText;
    private EditText mPhoneEditText;
    private Spinner mMaleSpinner;
    private CircleImageView mPhotoCircleImageView;
    private RelativeLayout relativeLayout;
    private TextView mNoneTextView;
    private ProgressButton mAddProgressButton;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.getData().getInt("case")) {
                case OK: {
                    mNoneTextView.setVisibility(View.GONE);
                    try {
                        JSONObject jsonObject = new JSONObject(msg.getData().getString("message"));
                        UserObject userObject = new UserObject(jsonObject);
                        getphoto(userObject.getUserId());
                        id = userObject.getUserId();
                        mPhoneEditText.setText(userObject.getPhoneNumber());
                        mSignatureEditText.setText(userObject.getSignature());
                        mAreaEditText.setText(userObject.getaddress());
                        mUserNameEditText.setText(userObject.getUsername());
                        if (userObject.getGender() == 1) {
                            mMaleSpinner.setSelection(1);
                        } else {
                            mMaleSpinner.setSelection(0);
                        }
                        relativeLayout.setVisibility(View.VISIBLE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case NOTOK:{
                    mNoneTextView.setVisibility(View.VISIBLE);
                    Log.d(TAG,"ERROR message;"+msg.getData().getString("message"));
                    break;
                }
                case PHOTO:{
                    msg.getData().getByteArray("photo");
                    Bitmap bm = BitmapFactory.decodeByteArray(msg.getData().getByteArray("photo"), 0, msg.getData().getByteArray("photo").length);
                    mPhotoCircleImageView.setImageBitmap(bm);
                    break;
                }
                default:
                    Toast.makeText(AddFriendsActivity.this, msg.getData().getString("message"), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_friends);
        init();
    }
    private void init(){
        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mSearchView = (EditText) findViewById(R.id.sv_main);
        mUserNameEditText = (EditText) findViewById(R.id.et_username);
        mAreaEditText = (EditText) findViewById(R.id.et_area);
        mSignatureEditText = (EditText) findViewById(R.id.et_sign);
        mPhoneEditText = (EditText) findViewById(R.id.et_phonenumber);
        mMaleSpinner = (Spinner) findViewById(R.id.s_male);
        mPhotoCircleImageView = (CircleImageView) findViewById(R.id.civ_photo);
        relativeLayout = (RelativeLayout) findViewById(R.id.rl_main);
        mNoneTextView = (TextView) findViewById(R.id.tv_none);
        mAddProgressButton = (ProgressButton) findViewById(R.id.pb_add);

        mAddProgressButton.setOnClickListener(this);
        mNoneTextView.setVisibility(View.INVISIBLE);
        relativeLayout.setVisibility(View.INVISIBLE);
        mPhotoCircleImageView.setEnabled(false);
        mMaleSpinner.setEnabled(false);
        mUserNameEditText.setEnabled(false);
        mAreaEditText.setEnabled(false);
        mSignatureEditText.setEnabled(false);
        mPhoneEditText.setEnabled(false);
        mBackImageView.setOnClickListener(this);
        mSearchView.setFocusable(true);
        mSearchView.requestFocus();
        mSearchView.addTextChangedListener(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.pb_add:{
                if(id == -1){
                    Toast.makeText(this,"错误",Toast.LENGTH_SHORT).show();
                }else{
                    addFriends(id);
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mNoneTextView.setVisibility(View.INVISIBLE);
        relativeLayout.setVisibility(View.INVISIBLE);
        id = -1;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", mSearchView.getText().toString());
                String result = HttpUtil.submitPostData(params,"utf-8", URLConfig.ACTION_SEARCH_FRIENDS);
                Log.d(TAG,"search:"+result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if(jsonObject.isNull("code")){
                        Bundle bundle = new Bundle();
                        bundle.putInt("case", OK);
                        bundle.putString("message", result);
                        Message message = new Message();
                        message.setData(bundle);
                        mHandler.sendMessage(message);
                    }else{
                        Bundle bundle = new Bundle();
                        bundle.putInt("case", NOTOK);
                        bundle.putString("message", jsonObject.getString("message"));
                        Message message = new Message();
                        message.setData(bundle);
                        mHandler.sendMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
    private void getphoto(final int id){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = HttpUtil.getPhpop(id);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                Bundle bundle = new Bundle();
                bundle.putByteArray("photo",baos.toByteArray());
                bundle.putInt("case", PHOTO);
                Message message = new Message();
                message.setData(bundle);
                mHandler.sendMessage(message);
            }
        }).start();
    }
    private void addFriends(final int id){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("friendId", ""+id);
                SharedPreferences sharedPreferences= getSharedPreferences("main",MODE_PRIVATE);
                String result = HttpUtil.submitPostDataWithCookie(params,sharedPreferences.getString(SharePreferencesConfig.COOKIE_STRING,""),URLConfig.ACTION_ADD_FRIENDS);
                Log.d(TAG,"add:"+result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if(jsonObject.isNull("code")){
                        Bundle bundle = new Bundle();
                        bundle.putInt("case", -1);
                        bundle.putString("message", "未知错误");
                        Message message = new Message();
                        message.setData(bundle);
                        mHandler.sendMessage(message);
                    }else{
                        Bundle bundle = new Bundle();
                        switch (jsonObject.getInt("code")){
                            case 0:
                                bundle.putString("message", "成功添加");
                                break;
                            default:
                                bundle.putString("message", jsonObject.getString("message"));
                                break;
                        }
                        Message message = new Message();
                        message.setData(bundle);
                        mHandler.sendMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
