package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;

import com.baidu.lbsapi.panoramaview.*;
import com.baidu.lbsapi.BMapManager;

import com.baidu.lbsapi.tools.CoordinateConverter;
import com.baidu.lbsapi.tools.Point;
import com.baidu.mapapi.model.LatLng;
import com.rdc.mymap.R;
import com.rdc.mymap.application.PanoramaDemoApplication;

public class PanoramaActivity extends Activity {

    private PanoramaView mPanoramaView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_panorama);
        init();
    }

    private void init() {
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
        mPanoramaView = (PanoramaView) findViewById(R.id.pv);
        //mPanoramaView.setPanorama("0100220000130817164838355J5");
        mPanoramaView.setPanorama(point.x, point.y);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPanoramaView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPanoramaView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPanoramaView.destroy();
    }
}
