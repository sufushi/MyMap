package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.search.route.BikingRouteLine;
import com.rdc.mymap.R;
import com.rdc.mymap.adapter.MyStepListAdapter;
import com.rdc.mymap.model.Node;
import com.rdc.mymap.utils.RoutePlanSearchUtil;
import com.rdc.mymap.utils.overlayutils.BikingRouteOverlay;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.rdc.mymap.config.WayConfig.BIKING;

public class BikingRoutePlanActivity extends Activity implements View.OnClickListener{

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private RoutePlanSearchUtil mRoutePlanSearchUtil;

    private ImageView mBackImageView;
    private TextView mRouteTextView;
    private TextView mDistanceTextView;
    private TextView mDurationTextView;
    private LinearLayout mNavigateLinearLayout;

    private String mDistance;
    private String mDuration;
    private String mStartCity;
    private String mEndCity;
    private String mStartPlace;
    private String mEndPlace;

    private PopupWindow mPopupWindow;
    private MyStepListAdapter mMyStepListAdapter;
    private List<BikingRouteLine> mBikingRouteLineList = new ArrayList<BikingRouteLine>();
    private List<String> mBikingStepList = new ArrayList<String>();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0 :
                    mDistanceTextView.setText(mDistance);
                    mDurationTextView.setText(mDuration);
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
        setContentView(R.layout.activity_biking_route_plan);
        init();
    }

    private void init() {
        mMapView = (MapView) findViewById(R.id.mv);
        for(int i = 0; i < mMapView.getChildCount(); i++) {
            View child = mMapView.getChildAt(i);
            if(child instanceof ImageView) {
                child.setVisibility(View.INVISIBLE);
            }
        }
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setOnMapStatusChangeListener(new MyMapStatusChangeListener());
        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mBackImageView.setOnClickListener(this);
        mRouteTextView = (TextView) findViewById(R.id.tv_biking_route);
        mRouteTextView.setOnClickListener(this);
        mDistanceTextView = (TextView) findViewById(R.id.tv_distance);
        mDurationTextView = (TextView) findViewById(R.id.tv_duration);
        mNavigateLinearLayout = (LinearLayout) findViewById(R.id.ll_navigate);
        mRoutePlanSearchUtil = new RoutePlanSearchUtil();
        Intent intent = getIntent();
        mStartPlace = intent.getStringExtra("start_place");
        mEndPlace = intent.getStringExtra("end_place");
        mRoutePlanSearchUtil.search(new Node("广州", mStartPlace), new Node("广州", mEndPlace), BIKING);
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    if((mBikingRouteLineList = mRoutePlanSearchUtil.getBikingRouteLineList()).size() > 0) {
                        break;
                    }
                }
                onGetBikingRouteLineList();
            }
        }.start();
    }

    private void onGetBikingRouteLineList() {
        BikingRouteOverlay bikingRouteOverlay = new MyBikingRouteOverlay(mBaiduMap);
        mBaiduMap.setOnMarkerClickListener(bikingRouteOverlay);
        bikingRouteOverlay.setData(mBikingRouteLineList.get(0));
        bikingRouteOverlay.addToMap();
        bikingRouteOverlay.zoomToSpan();
        for(BikingRouteLine bikingRouteLine : mBikingRouteLineList) {
            float distance = Math.round(bikingRouteLine.getDistance() / 10f) / 100f;
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            mDistance = decimalFormat.format(distance) + "公里";
            int hour = bikingRouteLine.getDuration() / 3600;
            int minute = bikingRouteLine.getDuration() / 60 % 60;
            mDuration = (hour == 0 ? "" : hour + "小时") + (minute == 0 ? "" : minute + "分");
            for(BikingRouteLine.BikingStep bikingStep : bikingRouteLine.getAllStep()) {
                mBikingStepList.add(bikingStep.getInstructions());
            }
        }
        mHandler.sendEmptyMessage(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back :
                finish();
                break;
            case R.id.tv_biking_route :
                showPopupWindow();
                break;
            case R.id.ll_navigate :
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
        ListView listView = (ListView) contentView.findViewById(R.id.lv_plan_route);
        mMyStepListAdapter = new MyStepListAdapter(mBikingStepList, this);
        listView.setAdapter(mMyStepListAdapter);
        View rootView = LayoutInflater.from(this).inflate(R.layout.activity_biking_route_plan, null);
        mPopupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
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

    private class MyBikingRouteOverlay extends BikingRouteOverlay {

        public MyBikingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public int getLineColor() {
            return Color.YELLOW;
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

}
