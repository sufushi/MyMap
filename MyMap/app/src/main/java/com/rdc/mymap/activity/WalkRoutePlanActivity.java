package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
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
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.rdc.mymap.R;
import com.rdc.mymap.adapter.MyStepListAdapter;
import com.rdc.mymap.model.Node;
import com.rdc.mymap.model.StepInfo;
import com.rdc.mymap.utils.GeoCoderUtil;
import com.rdc.mymap.utils.RoutePlanSearchUtil;
import com.rdc.mymap.utils.overlayutils.WalkingRouteOverlay;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.rdc.mymap.config.WayConfig.WALKING;

public class WalkRoutePlanActivity extends Activity implements View.OnClickListener{

    public static List<Activity> activityList = new LinkedList<Activity>();

    private static final String APP_FOLDER_NAME = "BNSDK";

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
    private String mSDCardPath;

    private PopupWindow mPopupWindow;
    private MyStepListAdapter mMyStepListAdapter;
    private List<WalkingRouteLine> mWalkingRouteLineList = new ArrayList<WalkingRouteLine>();
    private List<StepInfo> mWalkSteps = new ArrayList<StepInfo>();
    private List<LatLng> mWalkPoints = new ArrayList<LatLng>();

    private BNRoutePlanNode mStartBNRoutePlanNode;
    private BNRoutePlanNode mEndBNRoutePlanNode;
    private BaiduNaviManager.TTSPlayStateListener mTTSPlayStateListener = new BaiduNaviManager.TTSPlayStateListener() {
        @Override
        public void playStart() {
            Log.e("error", "TTS start");
        }

        @Override
        public void playEnd() {
            Log.e("error", "TTS end");
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0 :
                    mDistanceTextView.setText(mDistance);
                    mDurationTextView.setText(mDuration);
                    mMyStepListAdapter = new MyStepListAdapter(mWalkSteps, WalkRoutePlanActivity.this);
                    break;
                case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG :
                    break;
                case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG :
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityList.add(this);
        SDKInitializer.initialize(getApplicationContext());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_walking_route_plan);
        init();
        if (initDirs()) {
            initNavigate();
        }
    }

    private void initNavigate() {
        BNOuterTTSPlayerCallback bnOuterTTSPlayerCallback;
        BaiduNaviManager.getInstance().init(this, mSDCardPath, APP_FOLDER_NAME, new MyNaviInitListener(),
                null, mHandler, mTTSPlayStateListener);
    }

    private boolean initDirs() {
        mSDCardPath = getSDCardDir();
        if(mSDCardPath == null) {
            return false;
        }
        File file = new File(mSDCardPath, APP_FOLDER_NAME);
        if(!file.exists()) {
            file.mkdir();
        }
        return true;
    }

    private String getSDCardDir() {
        if(Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    private void initSetting() {
        BNaviSettingManager.setDayNightMode(BNaviSettingManager.DayNightMode.DAY_NIGHT_MODE_AUTO);
        BNaviSettingManager.setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
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
            mWalkSteps.add(new StepInfo(walkingRouteLine.getStarting().getTitle(), 0));
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
                mWalkSteps.add(new StepInfo(walkingStep.getInstructions(), walkingStep.getDirection()));
            }
            mWalkSteps.add(new StepInfo(walkingRouteLine.getTerminal().getTitle(), 0));
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
            case R.id.ll_navigate :
                prepareNavigate();
                Log.e("error", "click ll_navigate");
                break;
            default:
                break;
        }
    }

    private void prepareNavigate() {
        final GeoCoderUtil startGeoCoder = new GeoCoderUtil();
        final GeoCoderUtil endGeoCoder = new GeoCoderUtil();
        startGeoCoder.setAddress(mStartPlace);
        startGeoCoder.geoCode();
        endGeoCoder.setAddress(mEndPlace);
        endGeoCoder.geoCode();
        new Thread() {
            @Override
            public void run() {
                while(true) {
                    if(startGeoCoder.getLatLng() != null && endGeoCoder.getLatLng() != null) {
                        break;
                    }
                }
                routePlanToNavigate(startGeoCoder.getLatLng(), endGeoCoder.getLatLng());
            }
        }.start();
    }

    private void routePlanToNavigate(LatLng start, LatLng end) {
        double startLatitude = start.latitude;
        double startLongitude = start.longitude;
        double endLatitude = end.latitude;
        double endLongitude = end.longitude;
        Log.e("error", "geoCode:" + startLatitude + " " + startLongitude + " " + endLatitude + " " + endLongitude);
        mStartBNRoutePlanNode = new BNRoutePlanNode(startLongitude, startLatitude, mStartPlace, null);
        mEndBNRoutePlanNode = new BNRoutePlanNode(endLongitude, endLatitude, mEndPlace, null);
        if(mStartBNRoutePlanNode != null && mEndBNRoutePlanNode != null) {
            List<BNRoutePlanNode> bnRoutePlanNodeList = new ArrayList<BNRoutePlanNode>();
            bnRoutePlanNodeList.add(mStartBNRoutePlanNode);
            bnRoutePlanNodeList.add(mEndBNRoutePlanNode);
            BaiduNaviManager.getInstance().launchNavigator(this, bnRoutePlanNodeList, 1, true, new MyRoutePlanListener(mStartBNRoutePlanNode));
        }
    }

    private void showPopupWindow() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.layout_popwindow_route, null);
        mPopupWindow = new PopupWindow(contentView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setContentView(contentView);
        ImageView imageView = (ImageView) contentView.findViewById(R.id.iv_close);
        imageView.setOnClickListener(this);
        ListView listView = (ListView) contentView.findViewById(R.id.lv_plan_route);
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

    private class MyRoutePlanListener implements BaiduNaviManager.RoutePlanListener {

        private BNRoutePlanNode mBNRoutePlanNode;

        public MyRoutePlanListener(BNRoutePlanNode bnRoutePlanNode) {
            this.mBNRoutePlanNode = bnRoutePlanNode;
        }

        @Override
        public void onJumpToNavigator() {
            for(Activity activity : activityList) {
                if(activity.getClass().getName().endsWith("GuideActivity")) {
                    return;
                }
            }
            Intent intent = new Intent(WalkRoutePlanActivity.this, GuideActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("route_plan_node", mBNRoutePlanNode);
            intent.putExtras(bundle);
            intent.putExtra("way", "walk");
            startActivity(intent);
        }

        @Override
        public void onRoutePlanFailed() {
            Log.e("error", "routePlanFailed");
        }
    }

    private class MyNaviInitListener implements BaiduNaviManager.NaviInitListener {

        @Override
        public void onAuthResult(int i, String s) {
            if(i == 0) {
                Log.e("error", "key init success");
            } else {
                Log.e("error", "key init fail" + s);
            }
        }

        @Override
        public void initStart() {
            Log.e("error", "init start");
        }

        @Override
        public void initSuccess() {
            initSetting();
            Log.e("error", "init success");
        }

        @Override
        public void initFailed() {
            Log.e("error", "init failed");
        }
    }
}
