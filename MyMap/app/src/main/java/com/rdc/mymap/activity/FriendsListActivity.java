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
import android.widget.TextView;
import android.widget.Toast;

import com.rdc.mymap.R;
import com.rdc.mymap.adapter.ChatAdapter;
import com.rdc.mymap.adapter.FriendsListAdapter;
import com.rdc.mymap.config.SharePreferencesConfig;
import com.rdc.mymap.config.URLConfig;
import com.rdc.mymap.database.DataBaseHelper;
import com.rdc.mymap.model.FriendsListItem;
import com.rdc.mymap.model.FriendsListSortComparator;
import com.rdc.mymap.model.UserObject;
import com.rdc.mymap.utils.HttpUtil;
import com.rdc.mymap.view.SideBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by wsoyz on 2017/4/23.
 */

public class FriendsListActivity extends Activity implements View.OnClickListener, SideBar.OnTouchingLetterChangedListener, SideBar.OnTouchUpListener, AdapterView.OnItemClickListener {
    private final static String TAG = "FriendsListActivity";
    private final static int OK = 1;
    private final static int NOTOK = 2;


    private SharedPreferences sharedPreferences;
    private TextView mTipsTextView;
    private TextView mAddFriendsTextView;
    private ListView listView;
    private SideBar sb;
    private ImageView mBackIamgeView;
    private DataBaseHelper mDataBaseHelper;
    private List<UserObject> UserObjectlist;
    private List<FriendsListItem> list;
    private FriendsListAdapter adapter;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.getData().getInt("case")) {
                case OK: {
                    mDataBaseHelper = new DataBaseHelper(FriendsListActivity.this, "Data.db", 1);
                    UserObjectlist = mDataBaseHelper.getFriendsList();
                    list = new ArrayList<FriendsListItem>();
                    for (UserObject a : UserObjectlist) {
                        list.add(new FriendsListItem(a));
                    }
                    Comparator comp = new FriendsListSortComparator();
                    Collections.sort(list, comp);
                    adapter = new FriendsListAdapter(FriendsListActivity.this, list);
                    listView.setAdapter(adapter);
                    break;
                }
                case NOTOK:
                    break;
                default:
                    Toast.makeText(FriendsListActivity.this, msg.getData().getString("message"), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_friendslist);
        init();
    }

    private void init() {
        refreshFriend();
        mAddFriendsTextView = (TextView) findViewById(R.id.tv_add);
        mAddFriendsTextView.setOnClickListener(this);
        mTipsTextView = (TextView) findViewById(R.id.tv_tips);
        listView = (ListView) findViewById(R.id.lv_main);
        sb = (SideBar) findViewById(R.id.sb_main);
        mBackIamgeView = (ImageView) findViewById(R.id.iv_back);
        mBackIamgeView.setOnClickListener(this);

        mDataBaseHelper = new DataBaseHelper(FriendsListActivity.this, "Data.db", 1);
        UserObjectlist = mDataBaseHelper.getFriendsList();
        list = new ArrayList<FriendsListItem>();
        for (UserObject a : UserObjectlist) {
            list.add(new FriendsListItem(a));
        }
        Comparator comp = new FriendsListSortComparator();
        Collections.sort(list, comp);
        adapter = new FriendsListAdapter(FriendsListActivity.this, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        sb.setOnTouchingLetterChangedListener(this);
        sb.setOnTouchUpListener(this);
        sb.setTextView(mTipsTextView);
    }

    @Override
    protected void onResume() {
        refreshFriend();
        super.onResume();
    }

    private void refreshFriend() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mDataBaseHelper = new DataBaseHelper(FriendsListActivity.this, "Data.db", 1);
                sharedPreferences = getSharedPreferences("main", MODE_PRIVATE);
                String result = HttpUtil.submitGetDataWithCookie(sharedPreferences.getString(SharePreferencesConfig.COOKIE_STRING, ""), URLConfig.ACTION_GET_FRIENDS);
                Log.d(TAG, " result:" + result);
                if (result == "") {
                    Bundle bundle = new Bundle();
                    bundle.putInt("case", NOTOK);
                    bundle.putString("message", "网络错误");
                    Message message = new Message();
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                    return;
                } else {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        int i;
                        for (i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            mDataBaseHelper.saveUser(new UserObject(jsonObject));
                        }
                        Bundle bundle = new Bundle();
                        bundle.putInt("case", OK);
                        Message message = new Message();
                        message.setData(bundle);
                        mHandler.sendMessage(message);
                    } catch (JSONException e) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            Bundle bundle = new Bundle();
                            bundle.putInt("case", NOTOK);
                            bundle.putString("message", jsonObject.getString("message"));
                            Message message = new Message();
                            message.setData(bundle);
                            mHandler.sendMessage(message);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_add:
                startAddFriendsActivity();
                break;
        }
    }
    private void startChatActivity(){
    }
    private void startAddFriendsActivity(){
        Intent intent = new Intent(this, AddFriendsActivity.class);
        startActivity(intent);
    }
    @Override
    public void onTouchingLetterChanged(String s) {
        int i = adapter.getSelectPosition(s);
        if(i != -1) listView.smoothScrollToPosition(i);
//        listView.setSelection(adapter.getSelectPosition(s));
    }

    @Override
    public void onTouchUpListener() {

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, FriendsDetailsActivity.class);
        intent.putExtra("id",list.get(position).getUserObject().getUserId());
        startActivity(intent);
    }
}
