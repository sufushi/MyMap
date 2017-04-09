package com.rdc.mymap.utils;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

public class GeoCoderUtil {

    private LatLng mLatLng;
    private GeoCoder mGeoCoder;
    private String mAddress;
    private MyGetGeoCoderResultListener mMyGetGeoCoderResultListener;

    public GeoCoderUtil() {
        mGeoCoder = GeoCoder.newInstance();
        mMyGetGeoCoderResultListener = new MyGetGeoCoderResultListener();
        mGeoCoder.setOnGetGeoCodeResultListener(mMyGetGeoCoderResultListener);
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        this.mAddress = address;
    }

    public LatLng getLatLng() {
        return mLatLng;
    }

    public void setLatLng(LatLng latLng) {
        this.mLatLng = latLng;
    }

    public void reverseGeoCode() {
        mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(mLatLng));
    }

    public void geoCode() {
        mGeoCoder.geocode(new GeoCodeOption().address(mAddress));
    }

    public void destroy() {
        mGeoCoder.destroy();
    }

    private class MyGetGeoCoderResultListener implements OnGetGeoCoderResultListener {

        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
            if(geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                return;
            }
            mLatLng = geoCodeResult.getLocation();
        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
            if(reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                return;
            }
            mAddress = reverseGeoCodeResult.getAddress();
        }
    }
}
