package com.rdc.mymap.utils;

import com.baidu.mapapi.search.busline.BusLineResult;
import com.baidu.mapapi.search.busline.BusLineSearch;
import com.baidu.mapapi.search.busline.BusLineSearchOption;
import com.baidu.mapapi.search.busline.OnGetBusLineSearchResultListener;
import com.baidu.mapapi.search.core.SearchResult;

import java.util.ArrayList;
import java.util.List;

public class BusLineSearchUtil {

    private BusLineSearch mBusLineSearch;
    private MyGetBusLineSearchListener mMyGetBusLineSearchListener;
    private BusLineResult mBusLineResult;
    private List<BusLineResult> mBusLineResultList = new ArrayList<BusLineResult>();

    public BusLineSearchUtil() {
        mBusLineSearch = BusLineSearch.newInstance();
        mMyGetBusLineSearchListener = new MyGetBusLineSearchListener();
        mBusLineSearch.setOnGetBusLineSearchResultListener(mMyGetBusLineSearchListener);
    }

    public void searchBusLine(String  busLineId) {
        mBusLineSearch.searchBusLine(new BusLineSearchOption().city("广州市").uid(busLineId));
    }

    public void destroy() {
        mBusLineSearch.destroy();

    }

    public BusLineResult getBusLineResult() {
        return mBusLineResult;
    }

    public void setBusLineResult(BusLineResult busLineResult) {
        this.mBusLineResult = busLineResult;
    }

    public List<BusLineResult> getBusLineResultList() {
        return mBusLineResultList;
    }

    public void setBusLineResultList(List<BusLineResult> busLineResultList) {
        this.mBusLineResultList = busLineResultList;
    }

    private class MyGetBusLineSearchListener implements OnGetBusLineSearchResultListener {

        @Override
        public void onGetBusLineResult(BusLineResult busLineResult) {
            if(busLineResult.error != SearchResult.ERRORNO.NO_ERROR) {
                return;
            }
            mBusLineResult = busLineResult;
            mBusLineResultList.add(busLineResult);
        }
    }
}
