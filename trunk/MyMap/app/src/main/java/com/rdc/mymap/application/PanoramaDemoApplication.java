package com.rdc.mymap.application;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.baidu.lbsapi.BMapManager;
import com.baidu.lbsapi.MKGeneralListener;

public class PanoramaDemoApplication extends Application {

    private static PanoramaDemoApplication mInstance = null;
    public BMapManager bMapManager = null;

    public static PanoramaDemoApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        initEngineManger(this);
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
