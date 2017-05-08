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
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.lbsapi.panoramaview.*;
import com.baidu.lbsapi.BMapManager;

import com.baidu.lbsapi.tools.CoordinateConverter;
import com.baidu.lbsapi.tools.Point;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.platform.comapi.map.K;
import com.rdc.mymap.R;
import com.rdc.mymap.application.PanoramaDemoApplication;

public class PanoramaActivity extends Activity implements View.OnClickListener {

    private PanoramaView mPanoramaView;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private MapStatusUpdate mMapStatusUpdate;
    BitmapDescriptor mBitmapDescriptor;
    private OverlayOptions mOverlayOptions;
    private LatLng mLatlng;
    private ImageView mBackImageView;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0 :
                    mBaiduMap.clear();
                    float heading = (float) msg.arg1;
                    mOverlayOptions = new MarkerOptions().position(mLatlng).rotate(360 - heading).icon(mBitmapDescriptor);
                    mBaiduMap.addOverlay(mOverlayOptions);
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
        setContentView(R.layout.activity_panorama);
        init();
    }

    private void init() {
        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mBackImageView.setOnClickListener(this);
        PanoramaDemoApplication panoramaDemoApplication = (PanoramaDemoApplication) this.getApplication();
        if(panoramaDemoApplication.bMapManager == null) {
            panoramaDemoApplication.bMapManager = new BMapManager(panoramaDemoApplication);
            panoramaDemoApplication.bMapManager.init(new PanoramaDemoApplication.MyGeneralListener());
        }
        SharedPreferences posSharedPreferences = getSharedPreferences("position", Context.MODE_PRIVATE);
        LatLng latlng = new LatLng((double) posSharedPreferences.getFloat("latitude", 0),
                (double) posSharedPreferences.getFloat("longitude", 0));
        Point point = CoordinateConverter.converter(CoordinateConverter.COOR_TYPE.COOR_TYPE_GCJ02,
                new Point(latlng.latitude, latlng.longitude));
        Intent intent = getIntent();
        mPanoramaView = (PanoramaView) findViewById(R.id.pv);
        mPanoramaView.setPanoramaViewListener(new MyPanoramaViewListener());
        //mPanoramaView.setPanorama("0100220000130817164838355J5");
        //mPanoramaView.setPanorama(39.963175, 116.400244);
        //mPanoramaView.setPanorama(point.x, point.y);
        mPanoramaView.setPanorama(intent.getIntExtra("x", 0), intent.getIntExtra("y", 0));

        //LatLng sourceLatlng = new LatLng(intent.getFloatExtra("latitude", 0), intent.getFloatExtra("longitude", 0));
        mMapView = (MapView) findViewById(R.id.mv);
        int count = mMapView.getChildCount();
        for(int i = 0; i < count; i++) {
            View child = mMapView.getChildAt(i);
            if(child instanceof ImageView || child instanceof K) {
                child.setVisibility(View.INVISIBLE);
            }
        }
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setOnMapStatusChangeListener(new MyMapStatusChangeListener());
//        com.baidu.mapapi.utils.CoordinateConverter coordinateConverter = new com.baidu.mapapi.utils.CoordinateConverter();
//        coordinateConverter.coord(sourceLatlng);
//        coordinateConverter.from(com.baidu.mapapi.utils.CoordinateConverter.CoordType.COMMON);
//        LatLng resultLatLng = coordinateConverter.convert();
        LatLng resultLatlng = new LatLng(intent.getDoubleExtra("latitude", 0), intent.getDoubleExtra("longitude", 0));
        mLatlng = resultLatlng;
//        MyLocationData myLocationData = new MyLocationData.Builder()//
//                .latitude(mLatlng.latitude)
//                .longitude(mLatlng.longitude).build();
//        mBaiduMap.setMyLocationData(myLocationData);
        mBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.location_point);
//        MyLocationConfiguration myLocationConfiguration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, mBitmapDescriptor);
//        mBaiduMap.setMyLocationConfigeration(myLocationConfiguration);
        mMapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(resultLatlng, 18.0f);
        mBaiduMap.animateMapStatus(mMapStatusUpdate);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        mPanoramaView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        mPanoramaView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mPanoramaView.destroy();
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

    private class MyPanoramaViewListener implements PanoramaViewListener {

        @Override
        public void onDescriptionLoadEnd(String s) {

        }

        @Override
        public void onLoadPanoramaBegin() {

        }

        @Override
        public void onLoadPanoramaEnd(String s) {

        }

        @Override
        public void onLoadPanoramaError(String s) {

        }

        @Override
        public void onMessage(String s, int i) {
            Log.e("error", "onMessage=" + i);
            switch (i) {
                case 8213 :
                    Message message = new Message();
                    message.what = 0;
                    message.arg1 = (int) mPanoramaView.getPanoramaHeading();
                    mHandler.sendMessage(message);
                    break;
            }
        }

        @Override
        public void onCustomMarkerClick(String s) {

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
