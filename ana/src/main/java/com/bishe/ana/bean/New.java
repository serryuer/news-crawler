package com.bishe.ana.bean;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class New {

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String url;

    //内容
    private String context;
    //标题
    private String title;
    //作者
    private String author;
    //时间
    private String time;
    //时间戳
    private long times;
    //分词结果
    private Map<String, Double> words;
    //tfidf矩阵
    private Map<String, Double> metrics;

    //分词结果
    private Map<String, Double> titleWords;
    //tfidf矩阵
    private Map<String, Double> titleMetrics;
    //摘要
    private String abst;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    //所属网站
    private String tag;

    //所属话题
    private String topic;

    //所属类别
    public int getClassID() {
        return classID;
    }

    public void setClassID(int classID) {
        this.classID = classID;
    }

    private int classID;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }


    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    private List<String> keywords;

    public Map<String, Double> getTitleWords() {
        return titleWords;
    }

    public void setTitleWords(Map<String, Double> titleWords) {
        this.titleWords = titleWords;
    }

    public Map<String, Double> getTitleMetrics() {
        return titleMetrics;
    }

    public void setTitleMetrics(Map<String, Double> titleMetrics) {
        this.titleMetrics = titleMetrics;
    }


    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (time == null) {
            time = format.format(new Date());
        }
        this.time = time;
        if (!time.trim().equalsIgnoreCase("")) {
            try {
                Date date = format.parse(time);
                this.setTimes(date.getTime());
            } catch (ParseException e) {
                System.err.println("convert time str [" + time + "] failed");
                e.printStackTrace();
            }
        }

    }

    public long getTimes() {
        return times;
    }

    public void setTimes(long times) {
        this.times = times;
    }

    public Map<String, Double> getWords() {
        return words;
    }

    public void setWords(Map<String, Double> words) {
        this.words = words;
    }

    public Map<String, Double> getMetrics() {
        return metrics;
    }

    public void setMetrics(Map<String, Double> metrics) {
        this.metrics = metrics;
    }

    public String getAbst() {
        return abst;
    }

    public void setAbst(String abst) {
        this.abst = abst;
    }

    public void setWordsByJsonString(String jsonString) {
        JSONArray jsonArray = JSONArray.parseArray(jsonString);
        metrics = new HashMap<>();
        jsonArray.forEach(object -> {
            JSONObject jsonObject = (JSONObject) object;
            metrics.put(jsonObject.getString("word"), jsonObject.getDoubleValue("value"));
        });
    }

    public String getWordsJsonString() {
        JSONArray jsonArray = new JSONArray();
        metrics.forEach((word, tfidf) -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("word", word);
            jsonObject.put("value", tfidf);
            jsonArray.add(jsonObject);
        });
        return jsonArray.toJSONString();
    }


}
