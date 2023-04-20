package com.github.fearmygaze.mercury.model;

import java.util.HashMap;
import java.util.Map;

public class Message {
    public String id;
    public int type;
    public String sendByID;
    public String sendToID;
    public String content;
    public long date;
    public boolean showDate;


    public Message() {

    }

    public Message(String id, int type, String sendByID, String sendToID, String content) {
        this.id = id;
        this.type = type;
        this.sendByID = sendByID;
        this.sendToID = sendToID;
        this.content = content;

        this.showDate = false;
    }

    public Message(String id, int type, String sendByID, String sendToID, String content, long date) {
        this.id = id;
        this.type = type;
        this.sendByID = sendByID;
        this.sendToID = sendToID;
        this.content = content;
        this.date = date;

        this.showDate = false;
    }


    public void setShowDate(boolean showDate) {
        this.showDate = showDate;
    }

    public boolean isShowDateEnabled() {
        return showDate;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("type", type);
        map.put("sendBy", sendByID);
        map.put("sendTo", sendToID);
        map.put("content", content);
        map.put("date", date);
        return map;
    }


}
