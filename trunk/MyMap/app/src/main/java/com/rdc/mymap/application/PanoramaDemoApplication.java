package com.rdc.mymap.application;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.baidu.lbsapi.BMapManager;
import com.baidu.lbsapi.MKGeneralListener;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.trace.LBSTraceClient;

public class PanoramaDemoApplication extends Application {

    private static PanoramaDemoApplication mInstance = null;
    public BMapManager bMapManager = null;

    public LBSTraceClient lbsTraceClient;


    public static PanoramaDemoApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(getApplicationContext());
        mInstance = this;
        initEngineManger(this);
        lbsTraceClient = new LBSTraceClient(this);

    }

    private void initEngineManger(Context context) {
        if(bMapManager == null) {
            bMapManager = new BMapManager(context);
        }
        if(!bMapManager.init(new MyGeneralListener())) {
            Toast.makeText(PanoramaDemoApplication.getInstance().getApplicationContext(), "init error", Toast.LENGTH_LONG).show();
        }
    }

    public static class MyGeneralListener implements MKGeneralListener {

        @Override
        public void onGetPermissionState(int error) {
            if(error != 0) {
                Toast.makeText(PanoramaDemoApplication.getInstance().getApplicationContext(), "error:" + error, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(PanoramaDemoApplication.getInstance().getApplicationContext(), "success", Toast.LENGTH_LONG).show();
            }
        }
    }

}
