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
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.rdc.mymap.R;
import com.rdc.mymap.adapter.MyNearbyListAdapter;
import com.rdc.mymap.model.NearbyInfo;
import com.rdc.mymap.utils.PoiSearchUtil;

import java.util.ArrayList;
import java.util.List;

public class NearbyInfoActivity extends Activity implements View.OnClickListener {

    private TextView mTitleTextView;
    private ImageView mBackImageView;

    private ListView mNearbyInfoListView;
    private List<NearbyInfo> mNearbyInfoList = new ArrayList<NearbyInfo>();
    private MyNearbyListAdapter mMyNearbyListAdapter;

    private PoiSearchUtil mPoiSearchUtil;
    private List<PoiInfo> mPoiInfoList = new ArrayList<PoiInfo>();
    private List<PoiDetailResult> mPoiDetailResultList = new ArrayList<PoiDetailResult>();
    private List<String> mDetailUrlList = new ArrayList<String>();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0 :
                    mMyNearbyListAdapter = new MyNearbyListAdapter(mNearbyInfoList, NearbyInfoActivity.this);
                    mNearbyInfoListView.setAdapter(mMyNearbyListAdapter);
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
        setContentView(R.layout.activity_nearby_info);
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
        mTitleTextView = (TextView) findViewById(R.id.tv_title);
        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mBackImageView.setOnClickListener(this);

        mNearbyInfoListView = (ListView) findViewById(R.id.lv_nearby_info);
        mNearbyInfoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent webViewIntent = new Intent(NearbyInfoActivity.this, WebViewActivity.class);
                webViewIntent.putExtra("detailUrl", mDetailUrlList.get(position));
                webViewIntent.putExtra("title", mTitleTextView.getText());
                startActivity(webViewIntent);
            }
        });
        SharedPreferences posSharedPreferences = getSharedPreferences("position", Context.MODE_PRIVATE);
        LatLng latlng = new LatLng((double) posSharedPreferences.getFloat("latitude", 0),
                (double) posSharedPreferences.getFloat("longitude", 0));
        mPoiSearchUtil = new PoiSearchUtil();
        Intent intent = getIntent();
        Log.e("error", intent.getStringExtra("category"));
        switch (intent.getStringExtra("category")) {
            case "food" :
                searchNearby(latlng, "美食");
                break;
            case "interest" :
                searchNearby(latlng, "景点");
                break;
            case "hotel" :
                searchNearby(latlng, "旅馆");
                break;
            case "entertainment" :
                searchNearby(latlng, "娱乐");
                break;
            case "movie" :
                searchNearby(latlng, "电影");
                break;
            case "market" :
                searchNearby(latlng, "超市");
                break;
            case "wc" :
                searchNearby(latlng, "厕所");
                break;
            case "station" :
                searchNearby(latlng, "公交站");
                break;
            case "gas" :
                searchNearby(latlng, "加油站");
                break;
            case "pass" :
                searchNearby(latlng, "停车场");
                break;
            case "bank" :
                searchNearby(latlng, "银行");
                break;
            case "repair" :
                searchNearby(latlng, "维修");
                break;
            default:
                break;
        }

    }

    private void searchNearby(LatLng latlng, String category) {
        mTitleTextView.setText(category);
        mPoiSearchUtil.searchNearby(category, latlng, 1000);
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
            Log.e("error", "city:" + poiInfo.city + "address:" + poiInfo.address + "name:" + poiInfo.name + "uid:" + poiInfo.uid);
        }
        mPoiSearchUtil.searchPoiDetailResult();
        new Thread() {
            @Override
            public void run() {
                while(true) {
                    if((mPoiDetailResultList = mPoiSearchUtil.getPoiDetailResultList()).size() == mPoiInfoList.size()) {
                        Log.e("error", mPoiSearchUtil.getPoiDetailResultList().size() + "");
                        break;
                    }
                }
                try {
                    onGetPoiDetailResultList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void onGetPoiDetailResultList() {
        for(int i = 0; i < mPoiDetailResultList.size(); i ++) {
            PoiDetailResult poiDetailResult = mPoiDetailResultList.get(i);
            Log.e("error", "detailUrl:" + poiDetailResult.detailUrl);
            mDetailUrlList.add(poiDetailResult.detailUrl);
            NearbyInfo nearbyInfo = new NearbyInfo(poiDetailResult.getName(), null, poiDetailResult.getType(), (int) poiDetailResult.getFacilityRating(), poiDetailResult.getPrice(), poiDetailResult.getTelephone(), poiDetailResult.getAddress());
            mNearbyInfoList.add(nearbyInfo);
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
