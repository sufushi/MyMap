package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ZoomButton;
import android.widget.ZoomButtonsController;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.bikenavi.model.BikeRoutePlanError;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.platform.comapi.map.K;
import com.rdc.mymap.R;
import com.rdc.mymap.view.SatMenu;

public class MainActivity extends Activity implements SatMenu.OnSatMenuClickListener, View.OnClickListener{

    private SatMenu mSatMenu;
    private ImageView mUserImageView;
    private LinearLayout mBottomLinearLayout;

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private LatLng mCurLatLng;
    private BitmapDescriptor mCurMarker;
    private MapStatusUpdate mMapStatusUpdate;
    private LocationClient mLocationClient;
    private LocationClientOption mLocationClientOption;
    private BDLocationListener mBDLocationListener = new MyLocationListener();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0 :
                    startRouteActivity();
                    break;
                case 1 :
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

        mMapView = (MapView) findViewById(R.id.mv);
        int count = mMapView.getChildCount();
        for(int i = 0; i < count; i++) {
            View child = mMapView.getChildAt(i);
            if(child instanceof ImageView || child instanceof K) {
                child.setVisibility(View.INVISIBLE);
            }
        }
        //mMapView.getChildAt(2).setVisibility(View.GONE);

        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);

        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(mBDLocationListener);
        initLocationClientOption();
        mLocationClient.start();

    }

    private void initLocationClientOption() {
        mLocationClientOption = new LocationClientOption();
        mLocationClientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        mLocationClientOption.setCoorType("gcj02");
        mLocationClientOption.setScanSpan(10000);
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
    }

    @Override
    protected void onStop() {
        super.onStop();
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
        mMapView.onDestroy();
    }

    @Override
    public void onSatMenuClick(View view) {
        switch (view.getId()) {
            case R.id.sm_route :
                mHandler.sendEmptyMessageDelayed(0, 200);
                break;
            case R.id.sm_scan :
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
        switch (v.getId()) {
            case R.id.iv_user :
                Intent personCenterIntent = new Intent(MainActivity.this, PersonCenterActivity.class);
                startActivity(personCenterIntent);
                break;
            case R.id.ll_bottomBar :
                Intent nearbyIntent = new Intent(MainActivity.this, NearbyActivity.class);
                startActivity(nearbyIntent);
            default:
                break;
        }
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
            /*OverlayOptions overlayOptions = new MarkerOptions().position(latLng)//
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location))//
                .zIndex(4)//
                .draggable(false);
            mBaiduMap.addOverlay(overlayOptions);*/
            MyLocationData myLocationData = new MyLocationData.Builder()//
                    .accuracy(bdLocation.getRadius())//
                    .direction(bdLocation.getDirection())//
                    .latitude(latLng.latitude)
                    .longitude(latLng.longitude).build();
            mBaiduMap.setMyLocationData(myLocationData);
            mCurMarker = BitmapDescriptorFactory.fromResource(R.drawable.location);
            MyLocationConfiguration myLocationConfiguration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.COMPASS, true, mCurMarker);
            mBaiduMap.setMyLocationConfigeration(myLocationConfiguration);

            mMapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(latLng, 18.0f);
            mBaiduMap.animateMapStatus(mMapStatusUpdate);
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }
}
