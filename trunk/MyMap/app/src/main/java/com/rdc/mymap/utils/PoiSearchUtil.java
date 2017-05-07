package com.rdc.mymap.utils;

import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;

import java.util.ArrayList;
import java.util.List;

public class PoiSearchUtil {

    private PoiSearch mPoiSearch;
    private List<PoiInfo> mPoiInfoList;
    private List<PoiDetailResult> mPoiDetailResultList;
    private List<String> mBusLineIdList;
    private MyGetPoiSearchResultListener mMyGetPoiSearchResultListener;

    public PoiSearchUtil() {
        mPoiSearch = PoiSearch.newInstance();
        mMyGetPoiSearchResultListener = new MyGetPoiSearchResultListener();
        mPoiSearch.setOnGetPoiSearchResultListener(mMyGetPoiSearchResultListener);
        mPoiInfoList = new ArrayList<PoiInfo>();
        mPoiDetailResultList = new ArrayList<PoiDetailResult>();
        mBusLineIdList = new ArrayList<String>();
    }

    public List<PoiInfo> getPoiInfoList() {
        return mPoiInfoList;
    }

    public void setPoiInfoList(List<PoiInfo> poiInfoList) {
        this.mPoiInfoList = poiInfoList;
    }

    public List<PoiDetailResult> getPoiDetailResultList() {
        return mPoiDetailResultList;
    }

    public void setPoiDetailResultList(List<PoiDetailResult> poiDetailResultList) {
        this.mPoiDetailResultList = poiDetailResultList;
    }

    public List<String> getBusLineIdList() {
        return mBusLineIdList;
    }

    public void setBusLineIdList(List<String> busLineIdList) {
        this.mBusLineIdList = busLineIdList;
    }

    public void searchNearby(String keyword, LatLng latLng, int radius) {
        mPoiSearch.searchNearby(new PoiNearbySearchOption().keyword(keyword).location(latLng).radius(radius));
    }

    public void searchInCity(String keyword, String city) {
        mPoiSearch.searchInCity(new PoiCitySearchOption().city(city).keyword(keyword));
    }

    public void searchBound(String keyword, LatLng latLng) {
        LatLng southwest = new LatLng(latLng.latitude - 0.001, latLng.longitude - 0.0012);
        LatLng northeast = new LatLng(latLng.latitude + 0.001, latLng.longitude + 0.0012);
        LatLng southeast = new LatLng(latLng.latitude - 0.001, latLng.longitude + 0.0012);
        LatLng northwest = new LatLng(latLng.latitude + 0.001, latLng.longitude - 0.0012);
        LatLngBounds latLngBounds = new LatLngBounds.Builder().include(southwest).include(southeast).include(northwest).include(northeast).build();
        mPoiSearch.searchInBound(new PoiBoundSearchOption().bound(latLngBounds).keyword(keyword).pageCapacity(20));
    }

    public void searchPoiDetailResult() {
        if(mPoiInfoList != null) {
            for(int i = 0; i < mPoiInfoList.size(); i ++) {
                mPoiSearch.searchPoiDetail(new PoiDetailSearchOption().poiUid(mPoiInfoList.get(i).uid));
            }
        }
    }

    public void destroy() {
        mPoiSearch.destroy();
    }

    private class MyGetPoiSearchResultListener implements OnGetPoiSearchResultListener{

        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            if(poiResult.error != SearchResult.ERRORNO.NO_ERROR) {
                return;
            }
            mPoiInfoList.addAll(poiResult.getAllPoi());
            for(PoiInfo poiInfo : poiResult.getAllPoi()) {
                //Log.e("error", "type:" + poiInfo.type);
                if(poiInfo.type == PoiInfo.POITYPE.BUS_LINE || poiInfo.type == PoiInfo.POITYPE.SUBWAY_LINE) {
                    mBusLineIdList.add(poiInfo.uid);
                }
            }
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
            if(poiDetailResult.error != SearchResult.ERRORNO.NO_ERROR) {
                return;
            }
            mPoiDetailResultList.add(poiDetailResult);
        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    }
}
