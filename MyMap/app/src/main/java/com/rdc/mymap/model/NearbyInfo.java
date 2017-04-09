package com.rdc.mymap.model;

import android.graphics.Bitmap;

public class NearbyInfo {

    private String mRestaurant;
    private Bitmap mPicture;
    private String mKind;
    private int mRank;
    private double mCost;
    private String mTelephone;
    //private double mDistance;

    public NearbyInfo(String mRestaurant, Bitmap mPicture, String mKind, int mRank, double mCost, String telephone) {
        this.mRestaurant = mRestaurant;
        this.mPicture = mPicture;
        this.mKind = mKind;
        this.mRank = mRank;
        this.mCost = mCost;
        this.mTelephone = telephone;
        //this.mDistance = mDouble;
    }

    public String getRestaurant() {
        return mRestaurant;
    }

    public void setRestaurant(String mRestaurant) {
        this.mRestaurant = mRestaurant;
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

    public int getRank() {
        return mRank;
    }

    public void setRank(int mRank) {
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

    /*public double getDistance() {
        return mDistance;
    }

    public void setDistance(double ditance) {
        this.mDistance = ditance;
    }*/
}
