package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;

import com.baidu.mapapi.search.route.PlanNode;
import com.rdc.mymap.R;
import com.rdc.mymap.adapter.MyBusLineListAdapter;
import com.rdc.mymap.model.BusLineInfo;
import com.rdc.mymap.model.Node;
import com.rdc.mymap.utils.RoutePlanSearchUtil;

import java.util.ArrayList;
import java.util.List;

import static com.rdc.mymap.config.WayConfig.TRANSIT;

public class BusRoutePlanActivity extends Activity implements View.OnClickListener {

    private PlanNode mStartNode;
    private PlanNode mEndNode;
    private String mStartCity;
    private String mEndCity;
    private String mStartPlace;
    private String mEndPlace;

    private RoutePlanSearchUtil mRoutePlanSearchUtil;
    private List<BusLineInfo> mBusLineInfoList = new ArrayList<BusLineInfo>();
    private MyBusLineListAdapter mMyBusLineListAdapter;

    private ListView mBusLineListView;
    private ImageView mBackImageView;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0 :
                    mMyBusLineListAdapter = new MyBusLineListAdapter(mBusLineInfoList, BusRoutePlanActivity.this);
                    mBusLineListView.setAdapter(mMyBusLineListAdapter);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bus_route_plan);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mRoutePlanSearchUtil != null) {
            mRoutePlanSearchUtil.destroy();
        }
    }

    private void init() {
        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mBackImageView.setOnClickListener(this);
        mBusLineListView = (ListView) findViewById(R.id.lv_route_plan);
        mRoutePlanSearchUtil = new RoutePlanSearchUtil();
        Intent intent = getIntent();
        mStartPlace = intent.getStringExtra("start_place");
        mEndPlace = intent.getStringExtra("end_place");
        mRoutePlanSearchUtil.search(new Node("广州", mStartPlace), new Node("广州", mEndPlace), TRANSIT);
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    if((mBusLineInfoList = mRoutePlanSearchUtil.getBusLineInfoList()).size() == mRoutePlanSearchUtil.getMassTransitRouteLineList().size()
                            && mRoutePlanSearchUtil.getBusLineInfoList().size() > 0) {
                        break;
                    }
                }
                onRoutePlanSearch();
            }
        }.start();
    }

    private void onRoutePlanSearch() {
        mHandler.sendEmptyMessage(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back :
                finish();
        }
    }
}
