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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.DrivingRouteLine;
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
import com.rdc.mymap.utils.overlayutils.DrivingRouteOverlay;
import com.rdc.mymap.view.LoadingDialog;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.rdc.mymap.config.WayConfig.DRIVING;

public class DrivingRoutePlanActivity extends Activity implements View.OnClickListener {

    public static List<Activity> activityList = new LinkedList<Activity>();
    private static final String APP_FOLDER_NAME = "BNSDK";

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private RoutePlanSearchUtil mRoutePlanSearchUtil;

    private ImageView mBackImageView;
    private TextView mRouteTextView;
    private TextView mDistanceTextView;
    private TextView mDurationTextView;
    private TextView mTrafficNumTextView;
    private TextView mWayTextView;
    private LinearLayout mChangeLinearLayout;
    private RelativeLayout mDrivingNavigateRelativeLayout;
    private LoadingDialog mLoadingDialog;

    private String mStartCity;
    private String mEndCity;
    private String mStartPlace;
    private String mEndPlace;
    private LatLng mStartLatLng;
    private LatLng mEndLatLng;
    private int mCurWay;
    private String mSDCardPath;

    private PopupWindow mPopupWindow;
    private MyStepListAdapter mMyStepListAdapter;
    private List<DrivingRouteLine> mDrivingRouteLineList = new ArrayList<DrivingRouteLine>();
    private List<String> mDistanceList = new ArrayList<String>();
    private List<String> mDurationList = new ArrayList<String>();
    private List<String> mTrafficNumList = new ArrayList<String>();
    private List<List<StepInfo>> mDrivingStepsList = new ArrayList<List<StepInfo>>();

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
                    mDistanceTextView.setText(mDistanceList.get(mCurWay));
                    mDurationTextView.setText(mDurationList.get(mCurWay));
                    mTrafficNumTextView.setText(mTrafficNumList.get(mCurWay));
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        activityList.add(this);
        setContentView(R.layout.activity_driving_route_plan);
        init();
        if (initDirs()) {
            initNavigate();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mLoadingDialog != null) {
            if(mLoadingDialog.isShowing()) {
                mLoadingDialog.hide();
            }
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
        mRouteTextView = (TextView) findViewById(R.id.tv_driving_route);
        mRouteTextView.setOnClickListener(this);
        mDistanceTextView = (TextView) findViewById(R.id.tv_distance);
        mDurationTextView = (TextView) findViewById(R.id.tv_duration);
        mTrafficNumTextView = (TextView) findViewById(R.id.tv_traffic_num);
        mWayTextView = (TextView) findViewById(R.id.tv_way);
        mChangeLinearLayout = (LinearLayout) findViewById(R.id.ll_change);
        mChangeLinearLayout.setOnClickListener(this);
        mDrivingNavigateRelativeLayout = (RelativeLayout) findViewById(R.id.rl_navigate);
        mDrivingNavigateRelativeLayout.setOnClickListener(this);
        mLoadingDialog = new LoadingDialog(this);
        mRoutePlanSearchUtil = new RoutePlanSearchUtil();
        Intent intent = getIntent();
        mStartPlace = intent.getStringExtra("start_place");
        mEndPlace = intent.getStringExtra("end_place");
        mStartLatLng = new LatLng(intent.getDoubleExtra("start_latitude", 0), intent.getDoubleExtra("start_longitude", 0));
        mEndLatLng = new LatLng(intent.getDoubleExtra("end_latitude", 0), intent.getDoubleExtra("end_longitude", 0));
        mRoutePlanSearchUtil.search(new Node("广州", mStartPlace), new Node("广州", mEndPlace), DRIVING);
        new Thread() {
            @Override
            public void run() {
                while(true) {
                    if((mDrivingRouteLineList = mRoutePlanSearchUtil.getDrivingRouteLineList()).size() > 0) {
                        break;
                    }
                }
                onGetDrivingRouteLineList();
            }
        }.start();
    }

    private void onGetDrivingRouteLineList() {
        mBaiduMap.clear();
        mCurWay = 0;
        DrivingRouteOverlay drivingRouteOverlay = new MyDrivingRouteOverlay(mBaiduMap);
        mBaiduMap.setOnMarkerClickListener(drivingRouteOverlay);
        drivingRouteOverlay.setData(mDrivingRouteLineList.get(mCurWay));
        drivingRouteOverlay.addToMap();
        drivingRouteOverlay.zoomToSpan();
        for(DrivingRouteLine drivingRouteLine : mDrivingRouteLineList) {
            float distance = Math.round(drivingRouteLine.getDistance() / 10f) / 100f;
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            String distanceStr = decimalFormat.format(distance) + "公里";
            mDistanceList.add(distanceStr);
            int hour = drivingRouteLine.getDuration() / 3600;
            int minute = drivingRouteLine.getDuration() / 60 % 60;
            String duration = (hour == 0 ? "" : hour + "小时") + (minute == 0 ? "" : minute + "分");
            mDurationList.add(duration);
            mTrafficNumList.add("红绿灯" + drivingRouteLine.getLightNum() + "个");
            List<StepInfo> drivingSteps = new ArrayList<StepInfo>();
            for(DrivingRouteLine.DrivingStep drivingStep : drivingRouteLine.getAllStep()) {
                drivingSteps.add(new StepInfo(drivingStep.getInstructions(), drivingStep.getDirection()));
            }
            mDrivingStepsList.add(drivingSteps);
        }
        mHandler.sendEmptyMessage(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back :
                finish();
                break;
            case R.id.tv_driving_route :
                showPopupWindow();
                break;
            case R.id.ll_change :
                change();
                break;
            case R.id.rl_navigate :
                prepareNavigate();
                break;
            case R.id.iv_close :
                mPopupWindow.dismiss();
                break;
            default:
                break;
        }
    }

    private void prepareNavigate() {
        mLoadingDialog.setMessage("正在进入导航").show();
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
//                routePlanToNavigate(startGeoCoder.getLatLng(), endGeoCoder.getLatLng());
                routePlanToNavigate(mStartLatLng, mEndLatLng);
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
            //mHandler.sendEmptyMessage(3);
            List<BNRoutePlanNode> bnRoutePlanNodeList = new ArrayList<BNRoutePlanNode>();
            bnRoutePlanNodeList.add(mStartBNRoutePlanNode);
            bnRoutePlanNodeList.add(mEndBNRoutePlanNode);
            BaiduNaviManager.getInstance().launchNavigator(this, bnRoutePlanNodeList, 1, true, new MyRoutePlanListener(mStartBNRoutePlanNode));
        }
    }

    private void change() {
        mCurWay = (++ mCurWay) % mDistanceList.size();
        mWayTextView.setText("方案 " + (mCurWay + 1));
        mDistanceTextView.setText(mDistanceList.get(mCurWay));
        mDurationTextView.setText(mDurationList.get(mCurWay));
        mTrafficNumTextView.setText(mTrafficNumList.get(mCurWay));
        mBaiduMap.clear();
        DrivingRouteOverlay drivingRouteOverlay = new MyDrivingRouteOverlay(mBaiduMap);
        mBaiduMap.setOnMarkerClickListener(drivingRouteOverlay);
        drivingRouteOverlay.setData(mDrivingRouteLineList.get(mCurWay));
        drivingRouteOverlay.addToMap();
        drivingRouteOverlay.zoomToSpan();
    }

    private void showPopupWindow() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.layout_popwindow_route, null);
        mPopupWindow = new PopupWindow(contentView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setContentView(contentView);
        ImageView imageView = (ImageView) contentView.findViewById(R.id.iv_close);
        imageView.setOnClickListener(this);
        ListView listView = (ListView) contentView.findViewById(R.id.lv_plan_route);
        mMyStepListAdapter = new MyStepListAdapter(mDrivingStepsList.get(mCurWay), this);
        listView.setAdapter(mMyStepListAdapter);
        View rootView = LayoutInflater.from(this).inflate(R.layout.activity_walking_route_plan, null);
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

    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public int getLineColor() {
            return Color.RED;
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
            Intent intent = new Intent(DrivingRoutePlanActivity.this, GuideActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("route_plan_node", mBNRoutePlanNode);
            intent.putExtras(bundle);
            intent.putExtra("way", "drive");
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
