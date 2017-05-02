package com.rdc.mymap.model;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wsoyz on 2017/5/1.
 */

public class Ticket {
    private int busTicketId;
    private Long purchaseDate;
    private Long useDate;
    private int fare;
    private String busName;

    public Ticket(int busTicketId, Long purchaseDate, Long useDate, int fare, String busName) {
        this.busTicketId = busTicketId;
        this.purchaseDate = purchaseDate;
        this.useDate = useDate;
        this.fare = fare;
        this.busName = busName;
    }

    public Ticket(JSONObject jsonObject) {
        try {
            this.busTicketId = jsonObject.getInt("busTicketId");
            this.purchaseDate = jsonObject.getLong("purchaseDate");
            if(jsonObject.isNull("useDate")) this.useDate = new Long(0);
            else this.useDate = jsonObject.getLong("useDate");
            this.fare = jsonObject.getInt("fare");
            this.busName = jsonObject.getString("busName");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public int getBusTicketId() {
        return busTicketId;
    }

    public Long getPurchaseDate() {
        return purchaseDate;
    }


    public Long getUseDate() {
        return useDate;
    }


    public int getFare() {
        return fare;
    }


    public String getBusName() {
        return busName;
    }

}
