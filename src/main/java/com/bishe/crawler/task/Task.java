package com.bishe.crawler.task;

import com.bishe.crawler.util.URLUtil;

public class Task {

    private String url;
    private String host;
    private TaskStatus status;
    private int weight;
    private String tag;
    private int depth;

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }


    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }


    public enum TaskStatus {
        Created,
        Finished,
        Error
    }

    public Task() {
        this("", "", TaskStatus.Created, 1);
    }

    public Task(String url, String host, TaskStatus status, int weight) {
        this.url = url;
        this.host = host;
        this.status = status;
        this.weight = weight;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        this.setHost(URLUtil.getHost(url));
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}
