package com.rdc.mymap.model;

import com.rdc.mymap.database.DataBaseHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wsoyz on 2017/4/12.
 */

//    {"userId":1,"username":"admin","gender":true,"address":"address","phoneNumber":"13111111111","signature":"admin","money":0}
public class UserObject {
    private int userId = 0;
    private String username = "";
    private int gender = 0;
    private String address = "";
    private String phoneNumber = "";
    private String signature = "";
    private boolean isEmpty = true;

    public boolean isEmpty() {
        return isEmpty;
    }

    private void setEmpty(boolean empty) {
        isEmpty = empty;
    }

    public  UserObject(JSONObject jsonObject) {
        try {
            this.userId = jsonObject.getInt("userId");
            this.username = jsonObject.getString("username");
            if(jsonObject.getBoolean("gender") == true)this.gender = 1;
            else this.gender = 0;
            this.address = jsonObject.getString("address");
            this.phoneNumber = jsonObject.getString("phoneNumber");
            this.signature = jsonObject.getString("signature");
        } catch (JSONException e) {
            e.printStackTrace();
            setEmpty(true);
        }
        setEmpty(false);
    }

    public UserObject(int userId, String username, int gender, String address, String phoneNumber, String signature) {
        this.userId = userId;
        this.username = username;
        this.gender = gender;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.signature = signature;
        setEmpty(false);
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getaddress() {
        return address;
    }

    public void setaddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "UserObject{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", gender=" + gender +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", signature='" + signature + '\'' +
                ", isEmpty=" + isEmpty +
                '}';
    }
}
