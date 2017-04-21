package com.rdc.mymap.model;

import com.baidu.mapapi.model.LatLng;

public class Node {
    private String mCity;
    private String mPlace;
    private LatLng mLatLng;

    public Node(String city, String place, LatLng latLng) {
        this.mCity = city;
        this.mPlace = place;
        this.mLatLng = latLng;
    }

    public Node(String city, String place) {
        this.mCity = city;
        this.mPlace = place;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        this.mCity = city;
    }

    public String getPlace() {
        return mPlace;
    }

    public void setPlace(String place) {
        this.mPlace = place;
    }

    public LatLng getLatLng() {
        return mLatLng;
    }

    public void setLatLng(LatLng latLng) {
        this.mLatLng = latLng;
    }
}
