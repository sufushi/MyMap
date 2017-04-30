package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.rdc.mymap.R;
import com.rdc.mymap.adapter.MyBusStationListAdapter;
import com.rdc.mymap.model.BusStationInfo;

import java.util.ArrayList;
import java.util.List;


public class BusStationsActivity extends Activity implements View.OnClickListener{

    private ImageView mBackImageView;
    private TextView mTitleTextView;
    private TextView mDirection1TextView;
    private TextView mDirection2TextView;
    private TextView mTime1TextView;
    private TextView mTime2TextView;
    private ListView mBusStationsTextView;
    private TextView mBuyTicketTextView;
    private LinearLayout mDirection1LinearLayout;
    private LinearLayout mDirection2LinearLayout;
    private View mDirection1View;
    private View mDirection2View;

    private BusStationInfo mBusStationInfo;
    private List<String> mBusStationList = new ArrayList<String>();
    private MyBusStationListAdapter mMyBusStationListAdapter;
    private int mCurIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bus_stations);
        init();
    }

    private void init() {
        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mBackImageView.setOnClickListener(this);
        mTitleTextView = (TextView) findViewById(R.id.tv_title);
        mDirection1TextView = (TextView) findViewById(R.id.tv_direction1);
        mDirection2TextView = (TextView) findViewById(R.id.tv_direction2);
        mTime1TextView = (TextView) findViewById(R.id.tv_time1);
        mTime2TextView = (TextView) findViewById(R.id.tv_time2);
        mBusStationsTextView = (ListView) findViewById(R.id.lv_bus_stations);
        mBuyTicketTextView = (TextView) findViewById(R.id.tv_buy_ticket);
        mBuyTicketTextView.setOnClickListener(this);
        mDirection1LinearLayout = (LinearLayout) findViewById(R.id.ll_direction1);
        mDirection1LinearLayout.setOnClickListener(this);
        mDirection2LinearLayout = (LinearLayout) findViewById(R.id.ll_direction2);
        mDirection2LinearLayout.setOnClickListener(this);
        mDirection1View = findViewById(R.id.v_direction1);
        mDirection2View = findViewById(R.id.v_direction2);

        Intent intent = getIntent();
        mBusStationInfo = (BusStationInfo) intent.getSerializableExtra("bus_stations");
        mTitleTextView.setText(mBusStationInfo.getBusStationName());
        mDirection1TextView.setText(mBusStationInfo.getDirection1());
        mTime1TextView.setText(mBusStationInfo.getStartTime() + " - " + mBusStationInfo.getEndTime());
        if(mBusStationInfo.getBusStationList2() == null) {
            mDirection2LinearLayout.setVisibility(View.GONE);
        } else {
            mDirection2TextView.setText(mBusStationInfo.getDirection2());
            mTime2TextView.setText(mBusStationInfo.getStartTime() + " - " + mBusStationInfo.getEndTime());
        }
        mBusStationList = mBusStationInfo.getBusStationList1();
        mMyBusStationListAdapter = new MyBusStationListAdapter(mBusStationList, this);
        mBusStationsTextView.setAdapter(mMyBusStationListAdapter);
        mCurIndex = 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back :
                finish();
                break;
            case R.id.tv_buy_ticket :
                break;
            case R.id.ll_direction1 :
                if(mCurIndex != 0) {
                    mDirection1View.setBackgroundColor(Color.parseColor("#9ed209"));
                    mDirection2View.setBackgroundColor(Color.parseColor("#949292"));
                    mBusStationList = mBusStationInfo.getBusStationList1();
                    mMyBusStationListAdapter = new MyBusStationListAdapter(mBusStationList, this);
                    mBusStationsTextView.setAdapter(mMyBusStationListAdapter);
                    mMyBusStationListAdapter.notifyDataSetChanged();
                    mCurIndex = 0;
                }
                break;
            case R.id.ll_direction2 :
                if(mCurIndex != 1) {
                    mDirection1View.setBackgroundColor(Color.parseColor("#949292"));
                    mDirection2View.setBackgroundColor(Color.parseColor("#9ed209"));
                    mBusStationList = mBusStationInfo.getBusStationList2();
                    mMyBusStationListAdapter = new MyBusStationListAdapter(mBusStationList, this);
                    mBusStationsTextView.setAdapter(mMyBusStationListAdapter);
                    mMyBusStationListAdapter.notifyDataSetChanged();
                    mCurIndex = 1;
                }
                break;
            default:
                break;
        }
    }
}
