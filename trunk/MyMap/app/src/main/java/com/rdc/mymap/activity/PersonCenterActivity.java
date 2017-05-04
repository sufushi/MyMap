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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rdc.mymap.R;
import com.rdc.mymap.config.SharePreferencesConfig;
import com.rdc.mymap.config.URLConfig;
import com.rdc.mymap.database.DataBaseHelper;
import com.rdc.mymap.model.MessageObject;
import com.rdc.mymap.model.UserObject;
import com.rdc.mymap.utils.HttpUtil;
import com.rdc.mymap.view.CircleImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;


public class PersonCenterActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "PersonCenterActivity";
    private final int OK = 1;
    private final int NONE = 2;
    private final int NOTOK = 3;
    static final int REQUEST_QR_SCAN = 0x00001;

    private ImageView mBackImageView;
    private CircleImageView mPhotoCircleImageView;
    private TextView mUsernameTextView;
    private RelativeLayout mMessageRelativeLayout;
    private LinearLayout mWalletLinearlayout;
    private LinearLayout mTicketLinearlayout;
    private LinearLayout mFriendsLinearlayout;
    private LinearLayout mFootprintLinearLayout;
    private LinearLayout mCollectionLinearLayout;
    private TextView mUnReadTextView;

    private DataBaseHelper mDataBaseHelper;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private Timer timer;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.getData().getInt("case")) {
                case OK: {
                    mDataBaseHelper = new DataBaseHelper(PersonCenterActivity.this, "Data.db", 1);
                    int i = mDataBaseHelper.getUnReadNumber(-1);
                    if (i > 0) {
                        mUnReadTextView.setText(i + "");
                        mUnReadTextView.setVisibility(View.VISIBLE);
                    }
                    break;
                }
                case NONE: {
                    Log.d(TAG, " get Message Error:" + "NONE");
                    mUnReadTextView.setVisibility(View.INVISIBLE);
                    break;
                }
                default:
                    Toast.makeText(PersonCenterActivity.this, msg.getData().getString("message"), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_person_center);
        init();
    }

    @Override
    protected void onResume() {
        isLogin();
        super.onResume();
    }

    @Override
    protected void onPause() {
        if(timer != null) timer.cancel();
        super.onPause();
    }

    private void init() {
        mFriendsLinearlayout = (LinearLayout) findViewById(R.id.ll_friends);
        mFriendsLinearlayout.setOnClickListener(this);

        mUnReadTextView = (TextView) findViewById(R.id.tv_unread);
        mUnReadTextView.setVisibility(View.INVISIBLE);

        mMessageRelativeLayout = (RelativeLayout) findViewById(R.id.rl_message);
        mMessageRelativeLayout.setOnClickListener(this);

        mFootprintLinearLayout = (LinearLayout) findViewById(R.id.ll_footprint);
        mFootprintLinearLayout.setOnClickListener(this);

        mCollectionLinearLayout = (LinearLayout) findViewById(R.id.ll_collection);
        mCollectionLinearLayout.setOnClickListener(this);

        mWalletLinearlayout = (LinearLayout) findViewById(R.id.ll_wallet);
        mWalletLinearlayout.setOnClickListener(this);

        mTicketLinearlayout = (LinearLayout) findViewById(R.id.ll_ticket);
        mTicketLinearlayout.setOnClickListener(this);

        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mBackImageView.setOnClickListener(this);

        mPhotoCircleImageView = (CircleImageView) findViewById(R.id.civ_photo);
        mPhotoCircleImageView.setOnClickListener(this);

        mUsernameTextView = (TextView) findViewById(R.id.tv_username);
        mDataBaseHelper = new DataBaseHelper(this, "Data.db", 1);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.civ_photo:
                if (mPreferences.getBoolean(SharePreferencesConfig.ISLOGIN_BOOLEAN, false)) {
                    startDetailsActivity();
                } else {
                    startLoginActivity();
                }
                break;
            case R.id.rl_message:
                startMessageActivity();
                break;
            case R.id.ll_footprint:
                startActivityForResult(new Intent(PersonCenterActivity.this, CaptureActivity.class), REQUEST_QR_SCAN);
                break;
            case R.id.ll_wallet:
                if (mPreferences.getBoolean(SharePreferencesConfig.ISLOGIN_BOOLEAN, false)) {
                    startWalletActivity();
                } else {
                    Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ll_ticket:
                startTicketListActivity();
                break;
            case R.id.ll_friends:
                if (mPreferences.getBoolean(SharePreferencesConfig.ISLOGIN_BOOLEAN, false)) {
                    startFriendsListActivity();
                } else {
                    Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ll_collection:
                startCollectiontivity();
                break;
            default:
                break;
        }
    }

    private void startLoginActivity() {
        Intent intent = new Intent(PersonCenterActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void startDetailsActivity() {
        Intent intent = new Intent(PersonCenterActivity.this, DetailsActivity.class);
        startActivity(intent);
    }
    private void startTicketListActivity() {
        Intent intent = new Intent(PersonCenterActivity.this, TicketListActivity.class);
        startActivity(intent);
    }
    private void startMessageActivity() {
        Intent intent = new Intent(PersonCenterActivity.this, MessageListActivity.class);
        startActivity(intent);
    }

    private void startWalletActivity() {
        Intent intent = new Intent(PersonCenterActivity.this, WalletActivity.class);
        startActivity(intent);
    }

    private void startFriendsListActivity() {
        Intent intent = new Intent(PersonCenterActivity.this, FriendsListActivity.class);
        startActivity(intent);
    }
    private void startCollectiontivity() {
        Intent intent = new Intent(PersonCenterActivity.this, CollectionActivity.class);
        startActivity(intent);
    }
    private void isLogin() {
        mPreferences = getSharedPreferences("main", MODE_PRIVATE);
        mEditor = mPreferences.edit();
        if (mPreferences.getBoolean(SharePreferencesConfig.ISLOGIN_BOOLEAN, false)) {
            Log.d(TAG, "Logined!");
            Bitmap bm = mDataBaseHelper.getUserPhotoToBitmap(mPreferences.getInt(SharePreferencesConfig.ID_INT, -1));
            mUsernameTextView.setText(mPreferences.getString(SharePreferencesConfig.USERNAME_STRING, ""));
            if (bm != null) mPhotoCircleImageView.setImageBitmap(bm);
            refreshMessage();
            refreshFriend();
            timer = new Timer();
            timer.schedule(new PersonCenterActivity.RefreshMessageTimerTask(), 0, 10000);
        } else {
            refreshMessage();
            Log.d(TAG, "NotLogin!");
            mPhotoCircleImageView.setImageResource(R.drawable.pikaqiu);
            mUsernameTextView.setText("登录");
        }
    }

    private void LoginOut() {
        mEditor = mPreferences.edit();
        mEditor.clear();
        mEditor.commit();
        mDataBaseHelper.clear();
        mUnReadTextView.setVisibility(View.INVISIBLE);
    }
    class RefreshMessageTimerTask extends TimerTask {
        @Override
        public void run() {
            mPreferences = getSharedPreferences("main", MODE_PRIVATE);
            String result = HttpUtil.submitPostDataWithCookie(null, mPreferences.getString(SharePreferencesConfig.COOKIE_STRING, ""), URLConfig.ACTION_GET_MESSAGE);
            Log.d(TAG, " GetMessage result;" + result);
            if (result == "") {
                Log.d(TAG, " get Message Error:" + "网络错误");
                return;
            }
            if (result == "[]") {
                Bundle bundle = new Bundle();
                bundle.putInt("case", NONE);
                Message message = new Message();
                message.setData(bundle);
                mHandler.sendMessage(message);
                return;
            }
            try {
                JSONArray jsonArray = new JSONArray(result);
                mDataBaseHelper = new DataBaseHelper(PersonCenterActivity.this, "Data.db", 1);
                for (int i = 0; i < jsonArray.length(); i++) {
                    mDataBaseHelper.saveMessage(new MessageObject(jsonArray.getJSONObject(i)));
                }
                Bundle bundle = new Bundle();
                bundle.putInt("case", OK);
                Message message = new Message();
                message.setData(bundle);
                mHandler.sendMessage(message);
            } catch (JSONException e) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Log.d(TAG, " get Message Error:" + jsonObject.getString("message"));
//                            if(jsonObject.getInt("code") == 1)
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }
    }
    private void refreshMessage() {
        mDataBaseHelper = new DataBaseHelper(this, "Data.db", 1);
        int i = mDataBaseHelper.getUnReadNumber(-1);
        Log.d(TAG," fereshing message ! number:"+i);
        if (i > 0) {
            mUnReadTextView.setText(i + "");
            mUnReadTextView.setVisibility(View.VISIBLE);
        }else{
            mUnReadTextView.setVisibility(View.INVISIBLE);
        }
    }
    private void refreshFriend() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mDataBaseHelper = new DataBaseHelper(PersonCenterActivity.this, "Data.db", 1);
                mPreferences = getSharedPreferences("main", MODE_PRIVATE);
                String result = HttpUtil.submitGetDataWithCookie(mPreferences.getString(SharePreferencesConfig.COOKIE_STRING, ""), URLConfig.ACTION_GET_FRIENDS);
                Log.d(TAG, " result:" + result);
                if (result == "") {
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("case", NOTOK);
//                    bundle.putString("message", "网络错误");
//                    Message message = new Message();
//                    message.setData(bundle);
//                    mHandler.sendMessage(message);
                    return;
                } else {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        int i;
                        for (i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            mDataBaseHelper.saveUser(new UserObject(jsonObject));
                        }
//                        Bundle bundle = new Bundle();
//                        bundle.putInt("case", OK);
//                        Message message = new Message();
//                        message.setData(bundle);
//                        mHandler.sendMessage(message);
                    } catch (JSONException e) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
//                            Bundle bundle = new Bundle();
//                            bundle.putInt("case", NOTOK);
//                            bundle.putString("message", jsonObject.getString("message"));
//                            Message message = new Message();
//                            message.setData(bundle);
//                            mHandler.sendMessage(message);
                            return;
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    //二维码扫描结果回调
    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_QR_SCAN) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this,data.getStringExtra("dataUrl"),Toast.LENGTH_LONG).show();
            } else {
                //扫描失败
                Toast.makeText(PersonCenterActivity.this, "扫描失败", Toast.LENGTH_LONG).show();
            }
        }
    }
}
