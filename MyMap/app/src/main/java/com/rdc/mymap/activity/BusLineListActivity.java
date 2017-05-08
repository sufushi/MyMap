package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.search.busline.BusLineResult;
import com.rdc.mymap.R;
import com.rdc.mymap.adapter.MyBusInfoListAdapter;
import com.rdc.mymap.model.BusInfo;
import com.rdc.mymap.model.BusStationInfo;
import com.rdc.mymap.utils.BusLineSearchUtil;
import com.rdc.mymap.utils.PoiSearchUtil;
import com.rdc.mymap.view.LoadingView;

import java.util.ArrayList;
import java.util.List;

public class BusLineListActivity extends Activity implements View.OnClickListener{

    private ImageView mBackImageView;
    private TextView mTitleTextView;
    private ListView mBusLineListView;
    private LoadingView mLoadingView;
    private List<BusInfo> mBusInfoList = new ArrayList<BusInfo>();
    private MyBusInfoListAdapter mMyBusInfoListAdapter;
    private List<BusStationInfo> mBusStationInfoList = new ArrayList<BusStationInfo>();

    private PoiSearchUtil mPoiSearchUtil;
    private BusLineSearchUtil mBusLineSearchUtil;
    private List<String> mBusLineIdList = new ArrayList<String>();
    private List<BusLineResult> mBusLineResultList = new ArrayList<BusLineResult>();
    private String mDetails;
    private String[] mDetailList;
    private int mCount = 0;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0 :
                    mLoadingView.setVisibility(View.GONE);
                    mMyBusInfoListAdapter = new MyBusInfoListAdapter(mBusInfoList, BusLineListActivity.this);
                    mBusLineListView.setAdapter(mMyBusInfoListAdapter);
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
        setContentView(R.layout.activity_bus_line_list);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPoiSearchUtil != null) {
            mPoiSearchUtil.destroy();
        }
        if(mBusLineSearchUtil != null) {
            mBusLineSearchUtil.destroy();
        }
    }

    private void init() {
        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mBackImageView.setOnClickListener(this);
        mTitleTextView = (TextView) findViewById(R.id.tv_title);
        mTitleTextView.setText(getIntent().getStringExtra("title"));
        mLoadingView = (LoadingView) findViewById(R.id.loading_view);
        mLoadingView.setVisibility(View.VISIBLE);
        mBusLineListView = (ListView) findViewById(R.id.lv_bus_line);
        mBusLineListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(BusLineListActivity.this, BusStationsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("bus_stations", mBusStationInfoList.get(position));
                intent.putExtras(bundle);
                intent.putExtra("price", mBusInfoList.get(position).getBusPrice());
                intent.putExtra("title", mBusInfoList.get(position).getBusName());
                Log.e("error", mBusInfoList.get(position).getBusPrice() + "");
                startActivity(intent);
            }
        });

        mPoiSearchUtil = new PoiSearchUtil();
        mBusLineSearchUtil = new BusLineSearchUtil();

        Intent intent = getIntent();
        mDetails = intent.getStringExtra("details");
        mDetailList = mDetails.split(";");

        searchBusInfos();
    }

    private void searchBusInfos() {
        mPoiSearchUtil.searchInCity(mDetailList[mCount], "广州");
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    if((mBusLineIdList = mPoiSearchUtil.getBusLineIdList()).size() > 0) {
                        Log.e("error", "busLineId:" + mPoiSearchUtil.getBusLineIdList().size());
                        break;
                    }
                }
                onGetBusLineIdList();
            }
        }.start();
    }

    private void onGetBusLineIdList() {
        for(int i = 0; i < mBusLineIdList.size(); i ++) {
            Log.e("error", mBusLineIdList.get(i));
            mBusLineSearchUtil.searchBusLine(mBusLineIdList.get(i));
        }
        new Thread() {
            @Override
            public void run() {
                while(true) {
                    if((mBusLineResultList = mBusLineSearchUtil.getBusLineResultList()).size() == mBusLineIdList.size()) {
                        Log.e("error", "busLineResultList:" + mBusLineResultList.size());
                        break;
                    }
                }
                onGetBusLineResultList();
            }
        }.start();
    }

    private void onGetBusLineResultList() {
        BusInfo busInfo = null;
        BusStationInfo busStationInfo = null;
        for(int i = 0; i < mBusLineResultList.size(); i ++) {
            BusLineResult busLineResult = mBusLineResultList.get(i);
            Log.e("error", "name" + busLineResult.getBusLineName() +
                    "direction" + busLineResult.getLineDirection() +
                    "price" + busLineResult.getBasePrice() +
                    "startTime=" + busLineResult.getStartTime().getHours() + busLineResult.getStartTime().getMinutes() +
                    "stations" + busLineResult.getStations() +
                    "steps" + busLineResult.getSteps());
            List<String> busStations = new ArrayList<String>();
            for(BusLineResult.BusStation busStation : busLineResult.getStations()) {
                Log.e("error", "sattion title:" + busStation.getTitle());
                busStations.add(busStation.getTitle());
            }
            if(i == 0) {
                busStationInfo = new BusStationInfo(mDetailList[i], busLineResult.getStartTime().getHours() + ":" + busLineResult.getStartTime().getMinutes(),
                        busLineResult.getEndTime().getHours() + ":" + busLineResult.getEndTime().getMinutes(), busLineResult.getLineDirection(), null, busStations, null);
                busInfo = new BusInfo(mDetailList[mCount], busLineResult.getBasePrice(), busLineResult.getLineDirection(), "");
            } else {
                busStationInfo.setDirection2(busLineResult.getLineDirection());
                busStationInfo.setBusStationList2(busStations);
                busInfo.setDirection2(busLineResult.getLineDirection());
            }
        }
        mBusStationInfoList.add(busStationInfo);
        mBusInfoList.add(busInfo);
        mBusLineIdList.clear();
        mBusLineResultList.clear();
        mCount ++;
        if(mCount <= mDetailList.length - 1) {
            searchBusInfos();
        } else {
            mHandler.sendEmptyMessage(0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back :
                finish();
                break;
            default:
                break;
        }
    }
}
