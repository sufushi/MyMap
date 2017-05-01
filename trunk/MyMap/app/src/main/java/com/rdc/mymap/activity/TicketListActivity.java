package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.rdc.mymap.R;
import com.rdc.mymap.adapter.FriendsListAdapter;
import com.rdc.mymap.adapter.TicketListAdapter;
import com.rdc.mymap.config.SharePreferencesConfig;
import com.rdc.mymap.config.URLConfig;
import com.rdc.mymap.database.DataBaseHelper;
import com.rdc.mymap.model.FriendsListItem;
import com.rdc.mymap.model.FriendsListSortComparator;
import com.rdc.mymap.model.UserObject;
import com.rdc.mymap.utils.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by wsoyz on 2017/5/1.
 */

public class TicketListActivity extends Activity implements View.OnClickListener {
    private final static String TAG = "TicketListActivity";
    private final int OK = 1;
    private final int NOTOK = 2;
    private ImageView mBackIamgeView;
    private ListView listView;
    private TicketListAdapter adapter;
    private Cursor cursor;
    private DataBaseHelper dataBaseHelper;
    private SQLiteDatabase sqLiteDatabase;
    private SharedPreferences sharedPreferences;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.getData().getInt("case")) {
                case OK: {
                    dataBaseHelper = new DataBaseHelper(TicketListActivity.this, "Data.db", 1);
                    sqLiteDatabase = dataBaseHelper.getWritableDatabase();
                    cursor = sqLiteDatabase.query("Ticket", null, null, null, null, null, null);
                    if (cursor != null) {
                        adapter = new TicketListAdapter(TicketListActivity.this, cursor);
                        listView.setAdapter(adapter);
                    }
                    break;
                }
                case NOTOK:
                    break;
                default:
                    Toast.makeText(TicketListActivity.this, msg.getData().getString("message"), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_ticketlist);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init() {
        mBackIamgeView = (ImageView) findViewById(R.id.iv_back);
        mBackIamgeView.setOnClickListener(this);
        listView = (ListView) findViewById(R.id.lv_main);
        initData();
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    private void initData() {
        dataBaseHelper = new DataBaseHelper(this, "Data.db", 1);
        sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        cursor = sqLiteDatabase.query("Ticket", null, null, null, null, null, null);
        if (cursor != null) {
            adapter = new TicketListAdapter(this, cursor);
            listView.setAdapter(adapter);
        }
        refreshTicket();
    }

    private void refreshTicket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sharedPreferences = getSharedPreferences("main", MODE_PRIVATE);
                String result = HttpUtil.submitGetDataWithCookie(sharedPreferences.getString(SharePreferencesConfig.COOKIE_STRING, ""), URLConfig.ACTION_GET_TICKET);
                Log.d(TAG, " refreshTicket result:" + result);
                if (result == "") {
                    Log.d(TAG, " ERROR1");
                    return;
                } else if (result == "[]") {
                    Log.d(TAG, " ERROR2");
                    return;
                } else {
                    try {
                        dataBaseHelper = new DataBaseHelper(TicketListActivity.this, "Data.db", 1);
                        JSONArray jsonArray = new JSONArray(result);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            dataBaseHelper.saveTicket(jsonArray.getJSONObject(i));
                        }
                        Bundle bundle = new Bundle();
                        bundle.putInt("case", OK);
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
}
