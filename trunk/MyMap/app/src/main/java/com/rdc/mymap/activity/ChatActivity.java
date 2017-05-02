package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rdc.mymap.R;
import com.rdc.mymap.adapter.ChatAdapter;
import com.rdc.mymap.config.SharePreferencesConfig;
import com.rdc.mymap.config.URLConfig;
import com.rdc.mymap.database.DataBaseHelper;
import com.rdc.mymap.model.ChatModel;
import com.rdc.mymap.model.ItemModel;
import com.rdc.mymap.model.MessageObject;
import com.rdc.mymap.model.UserObject;
import com.rdc.mymap.utils.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wsoyz on 2017/4/23.
 */

public class ChatActivity extends Activity implements View.OnClickListener {
    private final static String TAG = "ChatActivity";
    private final static int OK = 1;
    private final static int NOTOK = 2;
    private final static int GET_DETAILS = 3;
    private ImageView mBackImageView;
    private TextView mTitleTextView;
    private EditText mMessageEditText;
    private RecyclerView mRecyclerView;
    private TextView mSendTextView;
    private ChatAdapter adapter;
    private String content;
    private int hostid = -1;
    private UserObject userObject;
    private DataBaseHelper dataBaseHelper;
    private SharedPreferences sharedPreferences;
    private Timer timer;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.getData().getInt("case")) {
                case OK: {
                    Log.d(TAG, " having new message!");
                    refreshData();
                    dataBaseHelper.readAllMessage(userObject.getUserId());
                    break;
                }
                case NOTOK: {
                    break;
                }
                case GET_DETAILS: {
                    mTitleTextView.setText(userObject.getUsername());
                    break;
                }
                default:
                    Toast.makeText(ChatActivity.this, msg.getData().getString("message"), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);
        init();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }

    private void init() {
        {
            sharedPreferences = getSharedPreferences("main", MODE_PRIVATE);
            hostid = sharedPreferences.getInt(SharePreferencesConfig.ID_INT, -1);
            if (hostid == -1) finish();
        }
        Intent intent = getIntent();
        dataBaseHelper = new DataBaseHelper(this, "Data.db", 1);
        userObject = dataBaseHelper.getUserObjict(intent.getIntExtra("id", -1));
        mTitleTextView = (TextView) findViewById(R.id.tv_title);
        if (userObject == null) {
            userObject = new UserObject(intent.getIntExtra("id", -1),"非好友",1,null,null,null);
        }
        mTitleTextView.setText(userObject.getUsername());

        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mMessageEditText = (EditText) findViewById(R.id.et_message);
        mSendTextView = (TextView) findViewById(R.id.tv_send);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_main);
        mBackImageView.setOnClickListener(this);


        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(adapter = new ChatAdapter(this));
        mSendTextView.setOnClickListener(this);

        initData();
        timer = new Timer();
        //每过一秒钟执行一次MyTimerTask任务
        timer.schedule(new RefreshTimerTask(), 0, 1000);
    }
//    private void initData() {
//        mSendTextView.setOnClickListener(new View.OnClickListener() {
//            @TargetApi(Build.VERSION_CODES.M)
//            @Override
//            public void onClick(View v) {
//                ArrayList<ItemModel> data = new ArrayList<>();
//                ChatModel model = new ChatModel();
//                model.setIcon("http://img.my.csdn.net/uploads/201508/05/1438760758_6667.jpg");
//                model.setContent(content);
//                data.add(new ItemModel(ItemModel.CHAT_B, model));
//                adapter.addAll(data);
//                mMessageEditText.setText("");
//                hideKeyBorad(mMessageEditText);
//            }
//        });
//
//    }

    private void hideKeyBorad(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_send:
                sendingMessage();
                break;
            default:
                break;
        }
    }


    private void initData() {
        List<MessageObject> list = dataBaseHelper.getMessage(userObject.getUserId());
        if (list == null) return;
        else {
            ArrayList<ItemModel> models = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                MessageObject messageObject = list.get(i);
                ChatModel model = new ChatModel();
                model.setContent(messageObject.getContext());
                if (messageObject.getIshost() == 1) {
                    model.setId(hostid);
                    models.add(new ItemModel(ItemModel.CHAT_B, model));
                } else {
                    model.setId(messageObject.getUserid());
                    models.add(new ItemModel(ItemModel.CHAT_A, model));
                }
            }
            adapter.replaceAll(models);
            mRecyclerView.smoothScrollToPosition(adapter.getItemCount());
            dataBaseHelper.readAllMessage(userObject.getUserId());
        }
    }

    private void sendingMessage() {
        content = mMessageEditText.getText().toString();
        MessageObject messageObject = new MessageObject(userObject.getUserId(), content, System.currentTimeMillis(), true, true);
        dataBaseHelper.saveMessage(messageObject);
        ArrayList<ItemModel> data = new ArrayList<>();
        ChatModel model = new ChatModel();
        model.setId(hostid);
        model.setContent(content);
        data.add(new ItemModel(ItemModel.CHAT_B, model));
        adapter.addAll(data);
        mMessageEditText.setText("");
        hideKeyBorad(mMessageEditText);
        mRecyclerView.smoothScrollToPosition(adapter.getItemCount());
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("context", content);
                params.put("accepterId", userObject.getUserId() + "");

                HttpUtil.submitPostDataWithCookie(params, sharedPreferences.getString(SharePreferencesConfig.COOKIE_STRING, ""), URLConfig.ACTION_SEND_MESSAGE);
            }
        }).start();
    }

    private void refreshData() {
        List<MessageObject> list = dataBaseHelper.getUnReadMessage(userObject.getUserId());
        if (list == null) {
            Log.d(TAG, " refreshData: is null");
            return;
        } else {
            ArrayList<ItemModel> data = new ArrayList<>();
            Log.d(TAG, " refreshData: is not null");
            for (int i = 0; i < list.size(); i++) {
                MessageObject messageObject = list.get(i);
                ChatModel model = new ChatModel();
                model.setId(userObject.getUserId());
                model.setContent(messageObject.getContext());
                data.add(new ItemModel(ItemModel.CHAT_A, model));
            }
            adapter.addAll(data);
        }
    }
    private void getUserObject(final int id){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", id+"");
                String result = HttpUtil.submitPostData(null,"utf-8", URLConfig.ACTION_SEARCH_FRIENDS_ID);
                Log.d(TAG, " GetUserMessage result;" + result);
                if (result == "") {
                    return;
                }  else {
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
    class RefreshTimerTask extends TimerTask {
        @Override
        public void run() {
            sharedPreferences = getSharedPreferences("main", MODE_PRIVATE);
            String result = HttpUtil.submitPostDataWithCookie(null, sharedPreferences.getString(SharePreferencesConfig.COOKIE_STRING, ""), URLConfig.ACTION_GET_MESSAGE);
            Log.d(TAG, " GetMessage result;" + result);
            if (result == "") {
                return;
            } else if (result.equals("[]")) {
                Log.d(TAG, " have not message!");
                return;
            } else {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    dataBaseHelper = new DataBaseHelper(ChatActivity.this, "Data.db", 1);
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
}
