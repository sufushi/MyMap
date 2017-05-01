package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
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
}
