package com.bishe.extraction.bean;

import com.alibaba.fastjson.JSONObject;

import java.sql.Timestamp;

public class WebNew {
    private Timestamp time;
    private String author;
    private String title;
    private String content;
    private int classID;
    private String timeStr;
    private String tag;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public int getClassID() {
        return classID;
    }

    public void setClassID(int classID) {
        this.classID = classID;
    }


    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }


    public String getTimeStr() {
        return timeStr;
    }

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }


    public WebNew() {
    }

    public WebNew(Timestamp time, String author, String title, String content) {
        this.time = time;
        this.author = author;
        this.title = title;
        this.content = content;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String toJsonString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", title);
        jsonObject.put("author", author);
        jsonObject.put("time", time.getTime());
        jsonObject.put("time_str", timeStr);
        jsonObject.put("content", content);
        jsonObject.put("class", classID);
        jsonObject.put("tag", tag);
        jsonObject.put("url", url);
        return jsonObject.toJSONString();
    }
}
