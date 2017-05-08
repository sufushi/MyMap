package com.rdc.mymap.activity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.NotificationCompat;
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
    private LinearLayout mAboutLinearLayout;
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
                    showNotification();
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
        if (timer != null) timer.cancel();
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

        mAboutLinearLayout = (LinearLayout) findViewById(R.id.ll_about);
        mAboutLinearLayout.setOnClickListener(this);

        mWalletLinearlayout = (LinearLayout) findViewById(R.id.ll_wallet);
        mWalletLinearlayout.setOnClickListener(this);

        mTicketLinearlayout = (LinearLayout) findViewById(R.id.ll_ticket);
        mTicketLinearlayout.setOnClickListener(this);

        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mBackImageView.setOnClickListener(this);

        mPhotoCircleImageView = (CircleImageView) findViewById(R.id.civ_photo);
        mPhotoCircleImageView.setOnClickListener(this);

        mUsernameTextView = (TextView) findViewById(R.id.tv_username);
        mUsernameTextView.setOnClickListener(this);
        mDataBaseHelper = new DataBaseHelper(this, "Data.db", 1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_username:
                if (mPreferences.getBoolean(SharePreferencesConfig.ISLOGIN_BOOLEAN, false)) {
                    startDetailsActivity();
                } else {
                    startLoginActivity();
                }
            case R.id.civ_photo:
                if (mPreferences.getBoolean(SharePreferencesConfig.ISLOGIN_BOOLEAN, false)) {
                    startDetailsActivity();
                } else {
                    startLoginActivity();
                }
                break;
            case R.id.rl_message:
                if (mPreferences.getBoolean(SharePreferencesConfig.ISLOGIN_BOOLEAN, false)) {
                    startMessageActivity();
                } else {
                    Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ll_footprint:
//                startActivityForResult(new Intent(PersonCenterActivity.this, CaptureActivity.class), REQUEST_QR_SCAN);
                startTraceHistoryActivity();
                break;
            case R.id.ll_wallet:
                if (mPreferences.getBoolean(SharePreferencesConfig.ISLOGIN_BOOLEAN, false)) {
                    startWalletActivity();
                } else {
                    Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ll_ticket:
                if (mPreferences.getBoolean(SharePreferencesConfig.ISLOGIN_BOOLEAN, false)) {
                    startTicketListActivity();
                } else {
                    Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ll_friends:
                if (mPreferences.getBoolean(SharePreferencesConfig.ISLOGIN_BOOLEAN, false)) {
                    startFriendsListActivity();
                } else {
                    Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ll_collection:
                startCollectionActivity();
                break;
            case R.id.ll_about:
                startAboutActivity();
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
    private void startTraceHistoryActivity() {
        Intent intent = new Intent(PersonCenterActivity.this, TraceHistoryActivity.class);
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

    private void startCollectionActivity() {
        Intent intent = new Intent(PersonCenterActivity.this, CollectionActivity.class);
        startActivity(intent);
    }
    private void startAboutActivity() {
        Intent intent = new Intent(PersonCenterActivity.this, AboutActivity.class);
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
            if (result.equals("[]")) {
                Log.d(TAG, " get Message Error:" + "[]");
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
        Log.d(TAG, " fereshing message ! number:" + i);
        if (i > 0) {
            mUnReadTextView.setText(i + "");
            mUnReadTextView.setVisibility(View.VISIBLE);
        } else {
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
                Toast.makeText(this, data.getStringExtra("dataUrl"), Toast.LENGTH_LONG).show();
            } else {
                //扫描失败
                Toast.makeText(PersonCenterActivity.this, "扫描失败", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showNotification(){
        Resources res=getResources();
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        Intent intent = new Intent(this,MessageListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        mBuilder.setContentTitle("你有新的消息")//设置通知栏标题
                .setContentText("点击查看详情") // span style = "font-family: Arial;" >/设置通知栏显示内容</span >
                .setContentIntent(pendingIntent) //设置通知栏点击意图
                //  .setNumber(number) //设置通知集合的数量
                .setTicker("你有新的消息~~") //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_ALL)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.logo))
                .setSmallIcon(R.drawable.message);//设置通知小ICON
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        manager.notify(1, mBuilder.build());
    }
}
