package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.TextView;

import com.baidu.lbsapi.panoramaview.*;
import com.baidu.lbsapi.BMapManager;

import com.baidu.lbsapi.tools.CoordinateConverter;
import com.baidu.lbsapi.tools.Point;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.rdc.mymap.R;
import com.rdc.mymap.application.PanoramaDemoApplication;

public class PanoramaActivity extends Activity {

    private PanoramaView mPanoramaView;
    private MapView mMapView;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

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
        mMapView = (MapView) findViewById(R.id.mv);
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
        //mPanoramaView.setPanorama("0100220000130817164838355J5");
        //mPanoramaView.setPanorama(39.963175, 116.400244);
        //mPanoramaView.setPanorama(point.x, point.y);
        mPanoramaView.setPanorama(intent.getIntExtra("x", 0), intent.getIntExtra("y", 0));
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
}
