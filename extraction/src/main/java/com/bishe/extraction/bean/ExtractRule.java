package com.bishe.extraction.bean;

import com.alibaba.fastjson.JSONObject;

import java.sql.Timestamp;

public class ExtractRule {
    private String timeid;
    private String authorid;
    private String titleid;
    private String contentid;

    public ExtractRule() {
    }

    public ExtractRule(String timeid, String authorid, String titleid, String contentid) {
        this.timeid = timeid;
        this.authorid = authorid;
        this.titleid = titleid;
        this.contentid = contentid;
    }

    public String toJsonString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("content", contentid);
        jsonObject.put("title", titleid);
        jsonObject.put("author", authorid);
        jsonObject.put("time", timeid);
        return jsonObject.toJSONString();
    }

    public static ExtractRule fromJsonString(String jsonString) {
        ExtractRule rule = new ExtractRule();
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        rule.setContentid(jsonObject.getString("content"));
        rule.setTitleid(jsonObject.getString("title"));
        rule.setTimeid(jsonObject.getString("time"));
        rule.setAuthorid(jsonObject.getString("author"));
        return rule;
    }

    public String getTimeid() {
        return timeid;
    }

    public void setTimeid(String timeid) {
        this.timeid = timeid;
    }

    public String getAuthorid() {
        return authorid;
    }

    public void setAuthorid(String authorid) {
        this.authorid = authorid;
    }

    public String getTitleid() {
        return titleid;
    }

    public void setTitleid(String titleid) {
        this.titleid = titleid;
    }

    public String getContentid() {
        return contentid;
    }

    public void setContentid(String contentid) {
        this.contentid = contentid;
    }

}
