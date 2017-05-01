package com.rdc.mymap.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wsoyz on 2017/4/25.
 */

public class MessageObject {
    private long date;
    private String context;
    private int userid;

    private int ishost;
    private int isread;
    private boolean isnull;

    public MessageObject(int userid,String context,long date,boolean isread,boolean ishost){
        if(userid <0 || date < 0) {
            isnull = true;
            return;
        }
        else isnull = false;
        this.userid = userid;
        this.context = context;
        this.date = date;
        if(isread) this.isread = 1;
        else this.isread = 0;
        if(ishost) this.ishost = 1;
        else this.ishost = 0;
    }
    public MessageObject(JSONObject jsonObject){
        if (jsonObject == null) {
            isnull = true;
            return;
        }else isnull = false;
        this.isread = 0;
        this.ishost = 0;
        try {
            this.userid = jsonObject.getInt("senderId");
            this.context = jsonObject.getString("context");
            this.date = jsonObject.getLong("date");
        } catch (JSONException e) {
            e.printStackTrace();
            isnull = true;
        }
    }
    public long getDate() {
        return date;
    }

    public String getContext() {
        return context;
    }

    public int getUserid() {
        return userid;
    }


    public int getIshost() {
        return ishost;
    }


    public int getIsread() {
        return isread;
    }

    public boolean isnull() {
        return isnull;
    }
}
