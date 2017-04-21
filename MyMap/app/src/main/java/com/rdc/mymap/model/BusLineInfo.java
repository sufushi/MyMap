package com.rdc.mymap.model;

import java.util.ArrayList;
import java.util.List;

public class BusLineInfo {

    private List<String> mBusLineList = new ArrayList<String>();
    private int mDuration;
    private int mStopNum;
    private int mDistance;
    private String mDepartureStation;

    public BusLineInfo(List<String> busLineList, int duration, int stopNum, int distance, String departureStation) {
        this.mBusLineList = busLineList;
        this.mDuration = duration;
        this.mStopNum = stopNum;
        this.mDistance = distance;
        this.mDepartureStation = departureStation;
    }

    public List<String> getBusLineList() {
        return mBusLineList;
    }

    public void setBusLineList(List<String> busLineList) {
        this.mBusLineList = busLineList;
    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int duration) {
        this.mDuration = duration;
    }

    public int getStopNum() {
        return mStopNum;
    }

    public void setStopNum(int stopNum) {
        this.mStopNum = stopNum;
    }

    public int getDistance() {
        return mDistance;
    }

    public void setDistance(int distance) {
        this.mDistance = distance;
    }

    public String getDepartureStation() {
        return mDepartureStation;
    }

    public void setDepartureStation(String departureStation) {
        this.mDepartureStation = departureStation;
    }
}
