package com.rdc.mymap.activity;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.baidu.mapapi.bikenavi.BikeNavigateHelper;
import com.baidu.mapapi.bikenavi.adapter.IBRouteGuidanceListener;
import com.baidu.mapapi.bikenavi.adapter.IBTTSPlayer;
import com.baidu.mapapi.bikenavi.model.BikeRouteDetailInfo;
import com.baidu.mapapi.bikenavi.model.RouteGuideKind;

public class BikeGuideActivity extends Activity {

    private BikeNavigateHelper mBikeNavigateHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBikeNavigateHelper.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBikeNavigateHelper.quit();
    }

    private void init() {
        mBikeNavigateHelper = BikeNavigateHelper.getInstance();
        View view = mBikeNavigateHelper.onCreate(BikeGuideActivity.this);
        if (view != null) {
            setContentView(view);
        }
        mBikeNavigateHelper.startBikeNavi(BikeGuideActivity.this);
        mBikeNavigateHelper.setTTsPlayer(new IBTTSPlayer() {
            @Override
            public int playTTSText(String s, boolean b) {
                return 0;
            }
        });
        mBikeNavigateHelper.setRouteGuidanceListener(this, new MyIBRouteGuidanceListener());
    }

    private class MyIBRouteGuidanceListener implements IBRouteGuidanceListener {

        @Override
        public void onRouteGuideIconUpdate(Drawable drawable) {

        }

        @Override
        public void onRouteGuideKind(RouteGuideKind routeGuideKind) {

        }

        @Override
        public void onRoadGuideTextUpdate(CharSequence charSequence, CharSequence charSequence1) {

        }

        @Override
        public void onRemainDistanceUpdate(CharSequence charSequence) {

        }

        @Override
        public void onRemainTimeUpdate(CharSequence charSequence) {

        }

        @Override
        public void onGpsStatusChange(CharSequence charSequence, Drawable drawable) {

        }

        @Override
        public void onRouteFarAway(CharSequence charSequence, Drawable drawable) {

        }

        @Override
        public void onRoutePlanYawing(CharSequence charSequence, Drawable drawable) {

        }

        @Override
        public void onReRouteComplete() {

        }

        @Override
        public void onArriveDest() {

        }

        @Override
        public void onVibrate() {

        }

        @Override
        public void onGetRouteDetailInfo(BikeRouteDetailInfo bikeRouteDetailInfo) {

        }
    }

}
