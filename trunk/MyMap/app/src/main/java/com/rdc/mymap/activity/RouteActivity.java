package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.busline.BusLineResult;
import com.rdc.mymap.R;
import com.rdc.mymap.adapter.MySearchHistoryAdapter;
import com.rdc.mymap.adapter.MyViewPagerAdapter;
import com.rdc.mymap.utils.BusLineSearchUtil;
import com.rdc.mymap.utils.PoiSearchUtil;
import com.rdc.mymap.view.UnderlineEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.rdc.mymap.config.WayConfig.BIKING;
import static com.rdc.mymap.config.WayConfig.DRIVING;
import static com.rdc.mymap.config.WayConfig.TRANSIT;
import static com.rdc.mymap.config.WayConfig.WALKING;

public class RouteActivity extends Activity implements View.OnClickListener{

    private ViewPager mViewPager;
    private TextView mBusTextView;
    private TextView mCarTextView;
    private TextView mBicycleTextView;
    private TextView mWalkTextView;
    private TextView mTaxiTextView;
    private List<View> mViewList;
    private int mPreIndex = 0;
    private int mCurIndex = 0;
    private View mBusView;
    private View mCarView;
    private View mBicycleView;
    private View mWalkView;
    private View mTaxiView;
    private ImageView mBackImageView;
    private ImageView mSearchImageView;
    private ImageView mPlusImageView;
    private LinearLayout mPassLinearLayout;
    private UnderlineEditText mStartNodeEditText;
    private TextView mEndEditText;
    private LinearLayout mNearbyBusLinearLayout;

    private ListView mBusSearchHistoryListView;
    private ListView mWalkSearchHistoryListView;
    private MySearchHistoryAdapter mMySearchHistoryAdapter;
    private List<String> mSearchHistoryList;
    private String[] mSearchHistory = {"大夫山森林公园", "广州大学城", "GoGo新天地", "珠江新城", "广州博物馆", "广州塔", "天河客运站", "白云山", "番禺广场", "万胜围"};

    private Boolean isExpand = false;

    private PoiSearchUtil mPoiSearchUtil;
    private BusLineSearchUtil mBusLineSearchUtil;
    private List<String> mBusLineIdList = new ArrayList<String>();
    private List<BusLineResult> mBusLineResultList = new ArrayList<BusLineResult>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_route);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPoiSearchUtil.destroy();
        mBusLineSearchUtil.destroy();
    }

    private void init() {
        initTextView();
        initViewPager();
        initImageView();
        initEditText();
        mNearbyBusLinearLayout = (LinearLayout) mBusView.findViewById(R.id.ll_nearby_bus);
        mNearbyBusLinearLayout.setOnClickListener(this);
        searchBusLineIdList();
     }

    private void initEditText() {
        mStartNodeEditText = (UnderlineEditText) findViewById(R.id.udt_start);
        mEndEditText = (TextView) findViewById(R.id.et_end);
        mStartNodeEditText.setOnClickListener(this);
        mEndEditText.setOnClickListener(this);
    }

    private void searchBusLineIdList() {
        mPoiSearchUtil = new PoiSearchUtil();
        mBusLineSearchUtil = new BusLineSearchUtil();
        SharedPreferences posSharedPreferences = getSharedPreferences("position", Context.MODE_PRIVATE);
        LatLng latlng = new LatLng((double) posSharedPreferences.getFloat("latitude", 0),
                (double) posSharedPreferences.getFloat("longitude", 0));
        //mPoiSearchUtil.searchNearby("车站", latlng, 1000);
        mPoiSearchUtil.searchInCity("384路", "广州市");
        new Thread() {
            @Override
            public void run() {
                while(true) {
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
        for(int i = 0; i < mBusLineResultList.size(); i ++) {
            BusLineResult busLineResult = mBusLineResultList.get(i);
            Log.e("error", "name" + busLineResult.getBusLineName() +
                    "direction" + busLineResult.getLineDirection() +
                    "price" + busLineResult.getBasePrice() +
                    "stations" + busLineResult.getStations() +
                    "steps" + busLineResult.getSteps());
        }
    }

    private void initImageView() {
        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mBackImageView.setOnClickListener(this);
        mSearchImageView = (ImageView) findViewById(R.id.iv_search);
        mSearchImageView.setOnClickListener(this);
        mPlusImageView = (ImageView) findViewById(R.id.iv_plus);
        mPlusImageView.setOnClickListener(this);
        mPassLinearLayout = (LinearLayout) findViewById(R.id.ll_pass_by);
    }

    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.vp);
        mViewList = new ArrayList<View>();
        LayoutInflater layoutInflater = getLayoutInflater();
        mBusView = layoutInflater.inflate(R.layout.layout_route_bus, null);
        mCarView = layoutInflater.inflate(R.layout.layout_route_car, null);
        mBicycleView = layoutInflater.inflate(R.layout.layout_route_bicycle, null);
        mWalkView = layoutInflater.inflate(R.layout.layout_route_walk, null);
        mTaxiView = layoutInflater.inflate(R.layout.layout_route_taxi, null);
        mViewList.add(mBusView);
        mViewList.add(mCarView);
        mViewList.add(mBicycleView);
        mViewList.add(mWalkView);
        mViewList.add(mTaxiView);
        mViewPager.setAdapter(new MyViewPagerAdapter(mViewList));
        mViewPager.setCurrentItem(0);
        mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());

        mBusSearchHistoryListView = (ListView) mBusView.findViewById(R.id.lv_search_history);
        mWalkSearchHistoryListView = (ListView) mWalkView.findViewById(R.id.lv_search_history);
        mSearchHistoryList = new ArrayList<String>();
        mSearchHistoryList = Arrays.asList(mSearchHistory);
        mMySearchHistoryAdapter = new MySearchHistoryAdapter(mSearchHistoryList, this);
        mBusSearchHistoryListView.setAdapter(mMySearchHistoryAdapter);
        mWalkSearchHistoryListView.setAdapter(mMySearchHistoryAdapter);

    }

    private void initTextView() {
        mBusTextView = (TextView) findViewById(R.id.tv_bus);
        mCarTextView = (TextView) findViewById(R.id.tv_car);
        mBicycleTextView = (TextView) findViewById(R.id.tv_bicycle);
        mWalkTextView = (TextView) findViewById(R.id.tv_walk);
        mTaxiTextView = (TextView) findViewById(R.id.taxi);

        mBusTextView.setOnClickListener(new MyOnClickListener(0));
        mCarTextView.setOnClickListener(new MyOnClickListener(1));
        mBicycleTextView.setOnClickListener(new MyOnClickListener(2));
        mWalkTextView.setOnClickListener(new MyOnClickListener(3));
        mTaxiTextView.setOnClickListener(new MyOnClickListener(4));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back :
                finish();
                break;
            case R.id.iv_plus :
                plus();
                break;
            case R.id.iv_search :
                startRoutePlanActivity();
                break;
            case R.id.udt_start :
                Intent startIntent = new Intent(RouteActivity.this, SearchLocationActivity.class);
                startActivityForResult(startIntent, 0);
                break;
            case R.id.et_end :
                Intent endIntent = new Intent(RouteActivity.this, SearchLocationActivity.class);
                startActivityForResult(endIntent, 2);
                break;
            case R.id.ll_nearby_bus :
                // TODO: 2017/4/19  
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0 :
                if(data != null && data.getStringExtra("Location") != null) {
                    mStartNodeEditText.setText(data.getStringExtra("Location"));
                }
                break;
            case 2 :
                if(data != null && data.getStringExtra("Location") != null) {
                    mEndEditText.setText(data.getStringExtra("Location"));
                }
                break;
            default:
                break;
        }
    }

    private void startRoutePlanActivity() {
        switch (mCurIndex) {
            case 0 :
                Intent busRoutePlanIntent = new Intent(RouteActivity.this, BusRoutePlanActivity.class);
                busRoutePlanIntent.putExtra("start_city", "广州");
                busRoutePlanIntent.putExtra("end_city", "广州");
                busRoutePlanIntent.putExtra("start_place", mStartNodeEditText.getText().toString());
                busRoutePlanIntent.putExtra("end_place", mEndEditText.getText().toString());
                busRoutePlanIntent.putExtra("way", TRANSIT);
                startActivity(busRoutePlanIntent);
                break;
            case 1 :
                Intent driveRoutePlanIntent = new Intent(RouteActivity.this, DrivingRoutePlanActivity.class);
                driveRoutePlanIntent.putExtra("start_city", "广州");
                driveRoutePlanIntent.putExtra("end_city", "广州");
                driveRoutePlanIntent.putExtra("start_place", mStartNodeEditText.getText().toString());
                driveRoutePlanIntent.putExtra("end_place", mEndEditText.getText().toString());
                driveRoutePlanIntent.putExtra("way", DRIVING);
                startActivity(driveRoutePlanIntent);
                break;
            case 2 :
                Intent bikingRoutePlanIntent = new Intent(RouteActivity.this, BikingRoutePlanActivity.class);
                bikingRoutePlanIntent.putExtra("start_city", "广州");
                bikingRoutePlanIntent.putExtra("end_city", "广州");
                bikingRoutePlanIntent.putExtra("start_place", mStartNodeEditText.getText().toString());
                bikingRoutePlanIntent.putExtra("end_place", mEndEditText.getText().toString());
                bikingRoutePlanIntent.putExtra("way", BIKING);
                startActivity(bikingRoutePlanIntent);
                break;
            case 3 :
                Intent walkRoutePlanIntent = new Intent(RouteActivity.this, WalkRoutePlanActivity.class);
                walkRoutePlanIntent.putExtra("start_city", "广州");
                walkRoutePlanIntent.putExtra("end_city", "广州");
                walkRoutePlanIntent.putExtra("start_place", mStartNodeEditText.getText().toString());
                walkRoutePlanIntent.putExtra("end_place", mEndEditText.getText().toString());
                walkRoutePlanIntent.putExtra("way", WALKING);
                startActivity(walkRoutePlanIntent);
                break;
            case 4 :
                break;
            default:
                break;
        }

    }

    private void plus() {
        if(!isExpand) {
            mPassLinearLayout.setVisibility(View.VISIBLE);
            RotateAnimation rotateAnimation = new RotateAnimation(0, 45f, Animation.RELATIVE_TO_SELF, 0.5f ,Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(200);
            rotateAnimation.setFillAfter(true);
            mPlusImageView.setAnimation(rotateAnimation);
        } else {
            mPassLinearLayout.setVisibility(View.GONE);
            RotateAnimation rotateAnimation = new RotateAnimation(45f, 0, Animation.RELATIVE_TO_SELF, 0.5f ,Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(200);
            rotateAnimation.setFillAfter(true);
            mPlusImageView.setAnimation(rotateAnimation);
        }
        isExpand = !isExpand;
    }

    private class MyOnClickListener implements View.OnClickListener {

        private int mIndex;

        public MyOnClickListener(int index) {
            this.mIndex = index;
        }

        @Override
        public void onClick(View v) {
            mViewPager.setCurrentItem(mIndex);
        }
    }

    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mCurIndex = mViewPager.getCurrentItem();
            if(mPreIndex == mCurIndex) {
                return;
            }
            switch (mCurIndex) {
                case 0 :
                    mBusTextView.setBackgroundColor(Color.parseColor("#556eea"));
                    break;
                case 1 :
                    mCarTextView.setBackgroundColor(Color.parseColor("#556eea"));
                    break;
                case 2 :
                    mBicycleTextView.setBackgroundColor(Color.parseColor("#556eea"));
                    break;
                case 3 :
                    mWalkTextView.setBackgroundColor(Color.parseColor("#556eea"));
                    break;
                case 4 :
                    mTaxiTextView.setBackgroundColor(Color.parseColor("#556eea"));
                    break;
                default:
                    break;
            }
            switch (mPreIndex) {
                case 0 :
                    mBusTextView.setBackgroundColor(Color.parseColor("#2d4ce6"));
                    break;
                case 1 :
                    mCarTextView.setBackgroundColor(Color.parseColor("#2d4ce6"));
                    break;
                case 2 :
                    mBicycleTextView.setBackgroundColor(Color.parseColor("#2d4ce6"));
                    break;
                case 3 :
                    mWalkTextView.setBackgroundColor(Color.parseColor("#2d4ce6"));
                    break;
                case 4 :
                    mTaxiTextView.setBackgroundColor(Color.parseColor("#2d4ce6"));
                    break;
                default:
                    break;
            }
            mPreIndex = mCurIndex;
        }
    }

}
