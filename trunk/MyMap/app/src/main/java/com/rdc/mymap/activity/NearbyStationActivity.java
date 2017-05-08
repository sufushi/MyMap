package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.rdc.mymap.R;
import com.rdc.mymap.adapter.MyNearbyStationListAdapter;
import com.rdc.mymap.model.NearbyInfo;
import com.rdc.mymap.utils.PoiSearchUtil;

import java.util.ArrayList;
import java.util.List;

public class NearbyStationActivity extends Activity implements View.OnClickListener{

    private ImageView mBackImageView;
    private ListView mNearbyStationListView;

    private List<NearbyInfo> mNearbyStationList = new ArrayList<NearbyInfo>();
    private MyNearbyStationListAdapter mMyNearbyStationListAdapter;

    private PoiSearchUtil mPoiSearchUtil;
    private List<PoiInfo> mPoiInfoList = new ArrayList<PoiInfo>();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0 :
                    mMyNearbyStationListAdapter = new MyNearbyStationListAdapter(mNearbyStationList, NearbyStationActivity.this);
                    mNearbyStationListView.setAdapter(mMyNearbyStationListAdapter);
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
        setContentView(R.layout.activity_nearby_station);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPoiSearchUtil != null) {
            mPoiSearchUtil.destroy();
        }
    }

    private void init() {
        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mBackImageView.setOnClickListener(this);
        mNearbyStationListView = (ListView) findViewById(R.id.lv_nearby_station);
        mNearbyStationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NearbyStationActivity.this, BusLineListActivity.class);
                intent.putExtra("title", mNearbyStationList.get(position).getName());
                intent.putExtra("details", mNearbyStationList.get(position).getAddress());
                startActivity(intent);
            }
        });

        mPoiSearchUtil = new PoiSearchUtil();
        SharedPreferences posSharedPreferences = getSharedPreferences("position", Context.MODE_PRIVATE);
        LatLng latlng = new LatLng((double) posSharedPreferences.getFloat("latitude", 0),
                (double) posSharedPreferences.getFloat("longitude", 0));
        mPoiSearchUtil.searchNearby("公交站", latlng, 1000);
        new Thread() {
            @Override
            public void run() {
                while(true) {
                    if((mPoiInfoList = mPoiSearchUtil.getPoiInfoList()).size() > 0) {
                        Log.e("error", (mPoiInfoList = mPoiSearchUtil.getPoiInfoList()).size() + "");
                        break;
                    }
                }
                onGetPoiInfoList();
            }
        }.start();
    }

    private void onGetPoiInfoList() {
        for(int i = 0; i < mPoiInfoList.size(); i ++) {
            PoiInfo poiInfo = mPoiInfoList.get(i);
            NearbyInfo nearbyInfo = new NearbyInfo(poiInfo.name, null, "", "", 0, "", poiInfo.address);
            mNearbyStationList.add(nearbyInfo);
            Log.e("error", "name:" + poiInfo.name + "address:" + poiInfo.address);
        }
        mHandler.sendEmptyMessage(0);
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
