package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
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
import com.rdc.mymap.model.MessageObject;
import com.rdc.mymap.utils.HttpUtil;
import com.rdc.mymap.utils.RCCode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wsoyz on 2017/5/1.
 */

public class QCCodeActivity extends Activity implements View.OnClickListener {
    private final static String TAG = "QCCodeActivity";
    private final static int OK =1;
    private final static int NOTOK = 2;

    private ImageView mBackImageView;
    private ImageView RCCodeImageView;
    private TextView mBusNameTextView;
    private TextView mFareTextView;
    private DataBaseHelper dataBaseHelper;
    private SQLiteDatabase sqLiteDatabase;
    private Timer timer;
    private int id = 0;
    private SharedPreferences sharedPreferences;private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.getData().getInt("case")) {
                case OK: {
                    refresh();
                    break;
                }
                case NOTOK: {
                    break;
                }
                default:
                    Toast.makeText(QCCodeActivity.this, msg.getData().getString("message"), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_rccode);
        init();
    }

    @Override
    protected void onResume() {

        timer = new Timer();
        //每过一秒钟执行一次MyTimerTask任务
        timer.schedule(new RefreshTimerTask(), 0, 1000);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }

    private void init(){
        Intent intent = getIntent();
        id = intent.getIntExtra("id",0);
        mBusNameTextView = (TextView) findViewById(R.id.tv_name);
        mFareTextView = (TextView) findViewById(R.id.tv_fare);
        dataBaseHelper = new DataBaseHelper(this,"Data.db",1);
        sqLiteDatabase = dataBaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query("Ticket",null,"busTicketId = ?",new String[]{id+""},null,null,null);
        if(cursor.moveToFirst()){
            mBusNameTextView.setText(cursor.getString(cursor.getColumnIndex("busName")));
            mFareTextView.setText(cursor.getInt(cursor.getColumnIndex("fare"))*1.0+"");
        }
        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mBackImageView.setOnClickListener(this);
        RCCodeImageView = (ImageView) findViewById(R.id.iv_main);
        Resources res=getResources();
        Bitmap logo= BitmapFactory.decodeResource(res, R.drawable.logo);
        RCCodeImageView.setImageBitmap(RCCode.createQRCodeWithLogo("www.walmt.cn/pipixia/useBusTicket?busTicketId="+id,300,logo));
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
    private void refresh(){
        dataBaseHelper = new DataBaseHelper(this,"Data.db",1);
        sqLiteDatabase = dataBaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query("Ticket",null,"busTicketId = ?",new String[]{id+""},null,null,null);
        cursor.moveToFirst();
        if (cursor.getLong(cursor.getColumnIndex("useDate")) != 0){
            Toast.makeText(this,"验票成功",Toast.LENGTH_LONG).show();
            Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(1000);
//            vibrator.cancel();
            finish();
        }
    }
    class RefreshTimerTask extends TimerTask {
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
                    dataBaseHelper = new DataBaseHelper(QCCodeActivity.this, "Data.db", 1);
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
    }
}
