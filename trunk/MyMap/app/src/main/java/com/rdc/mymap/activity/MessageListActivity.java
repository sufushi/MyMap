package com.rdc.mymap.activity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.rdc.mymap.R;
import com.rdc.mymap.adapter.MessageListAdapter;
import com.rdc.mymap.config.SharePreferencesConfig;
import com.rdc.mymap.config.URLConfig;
import com.rdc.mymap.database.DataBaseHelper;
import com.rdc.mymap.model.MessageObject;
import com.rdc.mymap.model.UserObject;
import com.rdc.mymap.utils.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wsoyz on 2017/4/26.
 */

public class MessageListActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private final static String TAG = "MessageListActivity";
    private final static int OK = 1;
    private final static int NONE = 2;
    private ImageView mBackImageView;
    private ListView listView;
    private MessageListAdapter adapter;
    private DataBaseHelper dataBaseHelper;
    private SharedPreferences mPreferences;
    private Timer timer;
    private List<UserObject> list;private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.getData().getInt("case")) {
                case OK:
                    getData();
                    showNotification();
                    break;
                case NONE: {
                    Log.d(TAG, " get Message Error:" + "NONE");
                    break;
                }
                default:
                    Toast.makeText(MessageListActivity.this, msg.getData().getString("message"), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_messagelist);
        init();
    }
    @Override
    protected void onResume() {
        getData();
        timer = new Timer();
        timer.schedule(new MessageListActivity.RefreshMessageTimerTask(), 0, 10000);
        super.onResume();
    }

    @Override
    protected void onPause() {
        timer.cancel();
        super.onPause();
    }

    private void init(){
        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        listView = (ListView) findViewById(R.id.lv_main);
        listView.setOnItemClickListener(this);
        mBackImageView.setOnClickListener(this);
    }
    private void getData(){
        dataBaseHelper = new DataBaseHelper(this,"Data.db",1);
        List<Integer> intList = dataBaseHelper.getMessageId();
        if(intList != null){
            list = new ArrayList<UserObject>();
            for (int i = 0;i < intList.size();i++){
                UserObject userObject = dataBaseHelper.getUserObjict(intList.get(i));
                if(userObject != null)list.add(userObject);
                else{
                    userObject = new UserObject(intList.get(i),"非通讯录好友",1,null,null,null);
                    list.add(userObject);
                }
            }
            if(list != null) {
                adapter = new MessageListAdapter(this,list);
                listView.setAdapter(adapter);
            }
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this,ChatActivity.class);
        intent.putExtra("id",list.get(position).getUserId());
        startActivity(intent);
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
                Bundle bundle = new Bundle();
                bundle.putInt("case", NONE);
                Message message = new Message();
                message.setData(bundle);
                mHandler.sendMessage(message);
                return;
            }
            try {
                JSONArray jsonArray = new JSONArray(result);
                dataBaseHelper = new DataBaseHelper(MessageListActivity.this, "Data.db", 1);
                for (int i = 0; i < jsonArray.length(); i++) {
                    dataBaseHelper.saveMessage(new MessageObject(jsonArray.getJSONObject(i)));
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
