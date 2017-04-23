package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteStep;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.rdc.mymap.R;
import com.rdc.mymap.adapter.MyStepListAdapter;
import com.rdc.mymap.model.Node;
import com.rdc.mymap.utils.RoutePlanSearchUtil;
import com.rdc.mymap.utils.overlayutils.WalkingRouteOverlay;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.rdc.mymap.config.WayConfig.WALKING;

public class WalkRoutePlanActivity extends Activity implements View.OnClickListener{

    private ImageView mBackImageView;
    private TextView mRouteTextView;
    private TextView mDistanceTextView;
    private TextView mDurationTextView;
    private LinearLayout mNavigateLinearLayout;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private RoutePlanSearchUtil mRoutePlanSearchUtil;

    private String mDistance;
    private String mDuration;
    private String mStartCity;
    private String mEndCity;
    private String mStartPlace;
    private String mEndPlace;

    private PopupWindow mPopupWindow;
    private MyStepListAdapter mMyStepListAdapter;
    private List<WalkingRouteLine> mWalkingRouteLineList = new ArrayList<WalkingRouteLine>();
    private List<String> mWalkSteps = new ArrayList<String>();
    private List<LatLng> mWalkPoints = new ArrayList<LatLng>();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0 :
                    mDistanceTextView.setText(mDistance);
                    mDurationTextView.setText(mDuration);
                    mMyStepListAdapter = new MyStepListAdapter(mWalkSteps, WalkRoutePlanActivity.this);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_walking_route_plan);
        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRoutePlanSearchUtil.destroy();
    }

    private void init() {
        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mBackImageView.setOnClickListener(this);
        mRouteTextView = (TextView) findViewById(R.id.tv_walk_route);
        mRouteTextView.setOnClickListener(this);
        mDistanceTextView = (TextView) findViewById(R.id.tv_distance);
        mDurationTextView = (TextView) findViewById(R.id.tv_duration);
        mNavigateLinearLayout = (LinearLayout) findViewById(R.id.ll_navigate);
        mNavigateLinearLayout.setOnClickListener(this);
        mMapView = (MapView) findViewById(R.id.mv);
        for(int i = 0; i < mMapView.getChildCount(); i++) {
            View child = mMapView.getChildAt(i);
            if(child instanceof ImageView) {
                child.setVisibility(View.INVISIBLE);
            }
        }
        mBaiduMap = mMapView.getMap();
        //mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setOnMapStatusChangeListener(new MyMapStatusChangeListener());
        mRoutePlanSearchUtil = new RoutePlanSearchUtil();
        Intent intent = getIntent();
        mStartPlace = intent.getStringExtra("start_place");
        mEndPlace = intent.getStringExtra("end_place");
        mRoutePlanSearchUtil.search(new Node("广州", mStartPlace), new Node("广州", mEndPlace), WALKING);
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    if((mWalkingRouteLineList = mRoutePlanSearchUtil.getWalkingRouteLineList()).size() > 0) {
                        break;
                    }
                }
                onGetWalkingRouteLineList();
            }
        }.start();
    }

    private void onGetWalkingRouteLineList() {
        //RouteLine routeLine = mWalkingRouteLineList.get(0);
        WalkingRouteOverlay walkingRouteOverlay = new MyWalkingRouteOverlay(mBaiduMap);
        mBaiduMap.setOnMarkerClickListener(walkingRouteOverlay);
        walkingRouteOverlay.setData(mWalkingRouteLineList.get(0));
        walkingRouteOverlay.addToMap();
        walkingRouteOverlay.zoomToSpan();
        for(WalkingRouteLine walkingRouteLine : mWalkingRouteLineList) {
            mWalkSteps.add(walkingRouteLine.getStarting().getTitle());
            float distance = Math.round(walkingRouteLine.getDistance() / 10f) / 100f;
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            mDistance = decimalFormat.format(distance) + "公里";
            int hour = walkingRouteLine.getDuration() / 3600;
            int minute = walkingRouteLine.getDuration() / 60 % 60;
            mDuration = (hour == 0 ? "" : hour + "小时") + (minute == 0 ? "" : minute + "分");
            Log.e("error", "duration=" + walkingRouteLine.getDuration() + "distance=" + walkingRouteLine.getDistance());
            Log.e("error", "start=" + walkingRouteLine.getStarting().getTitle() + "end=" + walkingRouteLine.getTerminal().getTitle());
            for(WalkingRouteLine.WalkingStep walkingStep : walkingRouteLine.getAllStep()) {
                Log.e("error", walkingStep.getInstructions());
                mWalkSteps.add(walkingStep.getInstructions());
            }
            mWalkSteps.add(walkingRouteLine.getTerminal().getTitle());
            for(RouteStep routeStep : walkingRouteLine.getAllStep()) {
                for(LatLng latLng : routeStep.getWayPoints()) {
                    Log.e("error", latLng.toString());
                    mWalkPoints.add(latLng);
                }
            }
        }
        mHandler.sendEmptyMessage(0);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_walk_route :
                showPopupWindow();
                break;
            case R.id.iv_back :
                finish();
                break;
            case R.id.iv_close :
                mPopupWindow.dismiss();
                break;
            default:
                break;
        }
    }

    private void showPopupWindow() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.layout_popwindow, null);
        mPopupWindow = new PopupWindow(contentView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setContentView(contentView);
        ImageView imageView = (ImageView) contentView.findViewById(R.id.iv_close);
        imageView.setOnClickListener(this);
        ListView listView = (ListView) contentView.findViewById(R.id.lv_walking_route);
        listView.setAdapter(mMyStepListAdapter);
        View rootView = LayoutInflater.from(this).inflate(R.layout.activity_walking_route_plan, null);
        mPopupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
    }

    private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public int getLineColor() {
            return Color.BLUE;
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.start_point);
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.end_point);
        }
    }

    private class MyMapStatusChangeListener implements BaiduMap.OnMapStatusChangeListener {

        @Override
        public void onMapStatusChangeStart(MapStatus mapStatus) {

        }

        @Override
        public void onMapStatusChange(MapStatus mapStatus) {

        }

        @Override
        public void onMapStatusChangeFinish(MapStatus mapStatus) {

        }
    }

}
