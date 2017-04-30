package com.rdc.mymap.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BusStationInfo implements Serializable{

    private String mBusStationName;
    private String mStartTime;
    private String mEndTime;
    private String mDirection1;
    private String mDirection2;
    private List<String> mBusStationList1 = new ArrayList<String>();
    private List<String> mBusStationList2 = new ArrayList<String>();

    public BusStationInfo(String busStationName, String startTime, String endTime, String direction1, String direction2, List<String> busStationList1, List<String> busStationList2) {
        this.mBusStationName = busStationName;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
        this.mDirection1 = direction1;
        this.mDirection2 = direction2;
        this.mBusStationList1 = busStationList1;
        this.mBusStationList2 = busStationList2;
    }

    public String getBusStationName() {
        return mBusStationName;
    }

    public void setBusStationName(String busStationName) {
        this.mBusStationName = busStationName;
    }

    public String getStartTime() {
        return mStartTime;
    }

    public void setStartTime(String startTime) {
        this.mStartTime = startTime;
    }

    public String getEndTime() {
        return mEndTime;
    }

    public void setEndTime(String endTime) {
        this.mEndTime = endTime;
    }

    public String getDirection1() {
        return mDirection1;
    }

    public void setDirection1(String direction1) {
        this.mDirection1 = direction1;
    }

    public String getDirection2() {
        return mDirection2;
    }

    public void setDirection2(String direction2) {
        this.mDirection2 = direction2;
    }

    public List<String> getBusStationList1() {
        return mBusStationList1;
    }

    public void setBusStationList1(List<String> busStationList1) {
        this.mBusStationList1 = busStationList1;
    }

    public List<String> getBusStationList2() {
        return mBusStationList2;
    }

    public void setBusStationList2(List<String> busStationList2) {
        this.mBusStationList2 = busStationList2;
    }
}
