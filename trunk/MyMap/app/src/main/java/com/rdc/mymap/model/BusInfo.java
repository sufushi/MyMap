package com.rdc.mymap.model;

public class BusInfo {

    private String mBusName;
    private float mBusPrice;
    private String mDirection1;
    private String mDirection2;

    public BusInfo(String busName, float busPrice, String direction1, String direction2) {
        this.mBusName = busName;
        this.mBusPrice = busPrice;
        this.mDirection1 = direction1;
        this.mDirection2 = direction2;
    }

    public String getBusName() {
        return mBusName;
    }

    public void setBusName(String busName) {
        this.mBusName = busName;
    }

    public float getBusPrice() {
        return mBusPrice;
    }

    public void setBusPrice(float busPrice) {
        this.mBusPrice = busPrice;
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
}
