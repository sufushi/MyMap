package com.rdc.mymap.model;

import android.graphics.Bitmap;

public class NearbyInfo {

    private String mName;
    private String mAddress;
    private Bitmap mPicture;
    private String mKind;
    private String mRank;
    private double mCost;
    private String mTelephone;
    //private double mDistance;

    public NearbyInfo(String mName, Bitmap mPicture, String mKind, String mRank, double mCost, String telephone, String address) {
        this.mName = mName;
        this.mAddress = address;
        this.mPicture = mPicture;
        this.mKind = mKind;
        this.mRank = mRank;
        this.mCost = mCost;
        this.mTelephone = telephone;
        //this.mDistance = distance;
    }

    public String getName() {
        return mName;
    }

    public void setmName(String name) {
        this.mName = name;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        this.mAddress = address;
    }

    public Bitmap getPicture() {
        return mPicture;
    }

    public void setPicture(Bitmap mPicture) {
        this.mPicture = mPicture;
    }

    public String getKind() {
        return mKind;
    }

    public void setKind(String mKind) {
        this.mKind = mKind;
    }

    public String getRank() {
        return mRank;
    }

    public void setRank(String mRank) {
        this.mRank = mRank;
    }

    public double getCost() {
        return mCost;
    }

    public void setCost(double mCost) {
        this.mCost = mCost;
    }

    public String getTelephone() {
        return mTelephone;
    }

    public void setTelephone(String telephone) {
        this.mTelephone = telephone;
    }

//    public double getDistance() {
//        return mDistance;
//    }
//
//    public void setDistance(double ditance) {
//        this.mDistance = ditance;
//    }
}
