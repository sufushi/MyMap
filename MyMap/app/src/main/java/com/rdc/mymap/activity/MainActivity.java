package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import android.widget.PopupWindow;

import com.baidu.lbsapi.model.BaiduPanoData;
import com.baidu.lbsapi.panoramaview.PanoramaRequest;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.platform.comapi.map.K;
import com.bumptech.glide.Glide;
import com.rdc.mymap.R;
import com.rdc.mymap.utils.PoiSearchUtil;
import com.rdc.mymap.view.SatMenu;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements SatMenu.OnSatMenuClickListener, View.OnClickListener{

    private SatMenu mSatMenu;
    private ImageView mUserImageView;
    private LinearLayout mBottomLinearLayout;
    private ImageView mTrafficModeImageView;
    private ImageView mOverviewModeImageView;
    private ImageView mSettingModeImageView;
    private ImageView mLocateImageView;
    private View mPanoramaView;
    private ImageView mPanoramaImageView;

    private Boolean isTrafficMode = false;
    private Boolean isSettingMode = false;
    private int mX;
    private int mY;
    private LatLng mPanoramaLatLng;
    private final String mBaseUrl = "http://pcsv1.map.bdimg.com/scape/?qt=pdata&pos=0_0&z=0&sid=";

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private LatLng mCurLatLng;
    private BitmapDescriptor mCurMarker;
    private MapStatusUpdate mMapStatusUpdate;
    private LocationClient mLocationClient;
    private LocationClientOption mLocationClientOption;
    private BDLocationListener mBDLocationListener = new MyLocationListener();
    private PoiSearchUtil mPoiSearchUtil;
    private List<PoiInfo> mPoiInfoList = new ArrayList<PoiInfo>();
    private PopupWindow mPopupWindow;
    private MyOrientationListener mMyOrientationListener;
    private float mCurrentX;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0 :
                    startRouteActivity();
                    break;
                case 1 :
                    showInfoWindow(msg);
                    break;
                case 2 :
                    break;
                case 3 :
                    break;
                default:
                    break;
            }
        }
    };

    private void showInfoWindow(Message msg) {
        String url = (String) msg.obj;
        Glide.with(MainActivity.this).load(url).into(mPanoramaImageView);
        InfoWindow infoWindow = new InfoWindow(mPanoramaView, mPanoramaLatLng, -57);
        mBaiduMap.showInfoWindow(infoWindow);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {

        mSatMenu = (SatMenu) findViewById(R.id.sm);
        mSatMenu.setOnSatMenuClickListener(this);
        mUserImageView = (ImageView) findViewById(R.id.iv_user);
        mUserImageView.setOnClickListener(this);
        mBottomLinearLayout = (LinearLayout) findViewById(R.id.ll_bottomBar);
        mBottomLinearLayout.setOnClickListener(this);
        mTrafficModeImageView = (ImageView) findViewById(R.id.iv_traffic_mode);
        mTrafficModeImageView.setOnClickListener(this);
        mOverviewModeImageView = (ImageView) findViewById(R.id.iv_overview_mode);
        mOverviewModeImageView.setOnClickListener(this);
        mSettingModeImageView = (ImageView) findViewById(R.id.iv_setting_mode);
        mSettingModeImageView.setOnClickListener(this);
        mLocateImageView = (ImageView) findViewById(R.id.iv_current_location);
        mLocateImageView.setOnClickListener(this);
        mPanoramaView = LayoutInflater.from(this).inflate(R.layout.layout_panorama_overlay, null);
        mPanoramaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPanoramaActivity();
            }
        });
        mPanoramaImageView = (ImageView) mPanoramaView.findViewById(R.id.iv_panorama);

        mMapView = (MapView) findViewById(R.id.mv);
        int count = mMapView.getChildCount();
        for(int i = 0; i < count; i++) {
            View child = mMapView.getChildAt(i);
            if(child instanceof ImageView || child instanceof K) {
                child.setVisibility(View.INVISIBLE);
            }
        }
        mMapView.showScaleControl(false);
        mMapView.getChildAt(2).setVisibility(View.GONE);

        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setOnMapStatusChangeListener(new MyMapStatusChangeListener());
        mBaiduMap.setOnMapClickListener(new MyOnMapClickListener());

        mPoiSearchUtil = new PoiSearchUtil();

        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(mBDLocationListener);
        initLocationClientOption();
        mLocationClient.start();

        mMyOrientationListener = new MyOrientationListener(this);
        mMyOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void OnOrientationChanged(float x) {
                mCurrentX = x;
            }
        });
    }

    private void initLocationClientOption() {
        mLocationClientOption = new LocationClientOption();
        mLocationClientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        mLocationClientOption.setCoorType("gcj02");
        //mLocationClientOption.setScanSpan(10000);
        mLocationClientOption.setIsNeedAddress(true);
        mLocationClientOption.setOpenGps(true);
        mLocationClientOption.setLocationNotify(true);
        mLocationClientOption.setIsNeedLocationDescribe(true);
        mLocationClientOption.setIsNeedLocationPoiList(true);
        //mLocationClientOption.setIgnoreKillProcess(false);

        mLocationClient.setLocOption(mLocationClientOption);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMyOrientationListener.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMyOrientationListener.stop();
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
        if(mPoiSearchUtil != null) {
            mPoiSearchUtil.destroy();
        }
        mMapView.onDestroy();
    }

    @Override
    public void onSatMenuClick(View view) {
        switch (view.getId()) {
            case R.id.sm_route :
                mHandler.sendEmptyMessageDelayed(0, 200);
                break;
            case R.id.sm_foot:
                break;
            case R.id.sm_bicycle :
                break;
            case R.id.sm_weather :
                break;
            default:
                break;
        }
    }

    private void startRouteActivity() {
        Intent intent = new Intent(MainActivity.this, RouteActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if(mSatMenu.isExpend()) {
            mSatMenu.change();
        }
        switch (v.getId()) {
            case R.id.iv_user :
                Intent personCenterIntent = new Intent(MainActivity.this, PersonCenterActivity.class);
                startActivity(personCenterIntent);
                break;
            case R.id.ll_bottomBar :
                Intent nearbyIntent = new Intent(MainActivity.this, NearbyActivity.class);
                startActivity(nearbyIntent);
            case R.id.iv_traffic_mode :
                isTrafficMode = !isTrafficMode;
                mBaiduMap.setTrafficEnabled(isTrafficMode);
                break;
            case R.id.iv_overview_mode :
                searchStreetPanorama();
                break;
            case R.id.iv_setting_mode :
                showSettingWindow();
                break;
            case R.id.iv_current_location :
                locate();
                break;
            case R.id.iv_normal_map :
                if(isSettingMode) {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                    mPopupWindow.dismiss();
                }
                break;
            case R.id.iv_compass_map :
                if(isSettingMode) {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);
                    mPopupWindow.dismiss();
                }
                break;
            case R.id.iv_satellite_map :
                if(isSettingMode) {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                    mPopupWindow.dismiss();
                }
                break;
            case R.id.v_close :
                if(isSettingMode) {
                    mPopupWindow.dismiss();
                }
                break;
            default:
                break;
        }
    }

    private void showSettingWindow() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.layout_popwindow_setting, null);
        mPopupWindow = new PopupWindow(contentView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setContentView(contentView);
        ImageView normalMap = (ImageView) contentView.findViewById(R.id.iv_normal_map);
        normalMap.setOnClickListener(this);
        ImageView compassMap = (ImageView) contentView.findViewById(R.id.iv_compass_map);
        compassMap.setOnClickListener(this);
        ImageView satelliteMap = (ImageView) contentView.findViewById(R.id.iv_satellite_map);
        satelliteMap.setOnClickListener(this);
        View closeView = (View) contentView.findViewById(R.id.v_close);
        closeView.setOnClickListener(this);
        View rootView = LayoutInflater.from(this).inflate(R.layout.activity_walking_route_plan, null);
        mPopupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
        isSettingMode = true;
    }

    private void locate() {
        CoordinateConverter coordinateConverter = new CoordinateConverter();
        coordinateConverter.coord(mCurLatLng);
        coordinateConverter.from(CoordinateConverter.CoordType.COMMON);
        LatLng latLng = coordinateConverter.convert();
        mMapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(latLng, 18.0f);
        mBaiduMap.animateMapStatus(mMapStatusUpdate);
    }

    private void searchStreetPanorama() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mPoiSearchUtil.searchNearby("小区", mCurLatLng, 500);
                while(true) {
                    if((mPoiInfoList = mPoiSearchUtil.getPoiInfoList()).size() > 0) {
                        Log.e("error", "size:" + mPoiSearchUtil.getPoiInfoList().size());
                        break;
                    }
                }
                for(PoiInfo poiInfo : mPoiInfoList) {
                    LatLng latLng = poiInfo.location;
                    PanoramaRequest panoramaRequest = PanoramaRequest.getInstance(MainActivity.this);
                    BaiduPanoData baiduPanoData = panoramaRequest.getPanoramaInfoByLatLon(latLng.longitude, latLng.latitude);
                    if(baiduPanoData.hasStreetPano()) {
                        String url = mBaseUrl + baiduPanoData.getPid();
                        mX = baiduPanoData.getX();
                        mY = baiduPanoData.getY();
                        //Point point = com.baidu.lbsapi.tools.CoordinateConverter.MCConverter2LL(mX, mY);
                        //mPanoramaLatLng = new LatLng(point.x, point.y);
                        mPanoramaLatLng = new LatLng(poiInfo.location.latitude, poiInfo.location.longitude);
                        Message message = new Message();
                        message.what = 1;
                        message.obj = url;
                        mHandler.sendMessage(message);
                        Log.e("error", baiduPanoData.getName());
                        break;
                    } else {
                        Log.e("error", "No street");
                    }
                }
            }
        }).start();
    }

    private void startPanoramaActivity() {
        Intent panoramaIntent = new Intent(MainActivity.this, PanoramaActivity.class);
        panoramaIntent.putExtra("x", mX);
        panoramaIntent.putExtra("y", mY);
        startActivity(panoramaIntent);
        Log.e("error", mX + "   " +  mY);
    }

    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if(bdLocation == null) {
                Log.e("error", "nullLocation");
                return;
            }
            mBaiduMap.clear();

            mCurLatLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude() - 0.0002);
            CoordinateConverter coordinateConverter = new CoordinateConverter();
            coordinateConverter.coord(mCurLatLng);
            coordinateConverter.from(CoordinateConverter.CoordType.COMMON);
            LatLng latLng = coordinateConverter.convert();

            SharedPreferences posSharedPreferences = getSharedPreferences("position", Context.MODE_PRIVATE);
            posSharedPreferences.edit().putFloat("longitude", (float) latLng.longitude)
            .putFloat("latitude", (float) latLng.latitude).commit();
            /*OverlayOptions overlayOptions = new MarkerOptions().position(latLng)//
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location))//
                .zIndex(4)//
                .draggable(false);
            mBaiduMap.addOverlay(overlayOptions);*/
            MyLocationData myLocationData = new MyLocationData.Builder()//
                    .accuracy(bdLocation.getRadius())//
                    .direction(mCurrentX)//
                    //.direction(bdLocation.getDirection())//
                    .latitude(latLng.latitude)
                    .longitude(latLng.longitude).build();
            mBaiduMap.setMyLocationData(myLocationData);
            mCurMarker = BitmapDescriptorFactory.fromResource(R.drawable.locate);
            MyLocationConfiguration myLocationConfiguration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, mCurMarker);
            mBaiduMap.setMyLocationConfigeration(myLocationConfiguration);

            mMapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(latLng, 18.0f);
            mBaiduMap.animateMapStatus(mMapStatusUpdate);
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

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

    private class MyOnMapClickListener implements BaiduMap.OnMapClickListener {

        @Override
        public void onMapClick(LatLng latLng) {
            if(mSatMenu.isExpend()) {
                mSatMenu.change();
            }
        }

        @Override
        public boolean onMapPoiClick(MapPoi mapPoi) {
            return false;
        }
    }

    public static class MyOrientationListener implements SensorEventListener {

        private SensorManager mSensorManager;
        private Sensor mSensor;
        private Context mContext;
        private float mLastX;
        private OnOrientationListener mOnOrientationListener;

        public MyOrientationListener(Context context) {
            this.mContext = context;
        }

        public void setOnOrientationListener(OnOrientationListener onOrientationListener) {
            this.mOnOrientationListener = onOrientationListener;
        }

        public void start() {
            mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
            if(mSensorManager != null) {
                mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
            }
            if(mSensor != null) {
                mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);
            }
        }

        public void stop() {
            mSensorManager.unregisterListener(this);
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                float x = event.values[SensorManager.DATA_X];
                if(Math.abs(x - mLastX) > 1.0) {
                    mOnOrientationListener.OnOrientationChanged(x);
                }
                mLastX = x;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        public interface OnOrientationListener {
            void OnOrientationChanged(float x);
        }
    }
 }
