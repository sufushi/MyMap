package com.rdc.mymap.model;

public class MyGridViewItem {

    private int mImageId;
    private String mName;

    public MyGridViewItem(int mImageId, String mName) {
        this.mImageId = mImageId;
        this.mName = mName;
    }

    public int getmImageId() {
        return mImageId;
    }

    public void setmImageId(int mImageId) {
        this.mImageId = mImageId;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }
}
