package com.bishe.ana.bean;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class Topic {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;

    public int getClassID() {
        return classID;
    }

    public void setClassID(int classID) {
        this.classID = classID;
    }

    private int classID;


    //话题名
    private String name;
    //创建时间
    private Timestamp createTime;
    //最近更新时间
    private Timestamp lastUpdateTime;
    //话题表示模型
    private Map<String, Double> words;

    public Map<String, Double> getWords() {
        return words;
    }

    public String getWordsJsonString() {
        JSONArray jsonArray = new JSONArray();
        words.forEach((word, tfidf) -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("word", word);
            jsonObject.put("value", tfidf);
            jsonArray.add(jsonObject);
        });
        return jsonArray.toJSONString();
    }

    public void setWordsByJsonString(String jsonString) {
        JSONArray jsonArray = JSONArray.parseArray(jsonString);
        words = new HashMap<>();
        jsonArray.forEach(object -> {
            JSONObject jsonObject = (JSONObject) object;
            words.put(jsonObject.getString("word"), jsonObject.getDoubleValue("value"));
        });
    }

    public void setWords(Map<String, Double> words) {
        this.words = words;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    private boolean isValid;

    public Topic() {
    }

    public Topic(String name, Timestamp createTime, Timestamp lastUpdateTime) {
        this.name = name;
        this.createTime = createTime;
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Timestamp lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }


}
