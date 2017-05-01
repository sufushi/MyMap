package com.rdc.mymap.model;

import java.io.Serializable;

/**
 * Created by WangChang on 2016/4/28.
 */
public class ChatModel implements Serializable{
    private int  id= -1;
    private String content="";
    private String type="";

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
