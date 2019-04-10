package com.bishe.crawler.manage;

import com.alibaba.fastjson.JSONObject;

public class NodeBean {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;

    private String ip;
    private String host;
    private String nodeName;
    private int status;//0 means half-dead, 1 means alive, -1 means definitely dead
    private long lastHeartBeatTime;

    public long getLastHeartBeatTime() {
        return lastHeartBeatTime;
    }

    public void setLastHeartBeatTime(long lastHeartBeatTime) {
        this.lastHeartBeatTime = lastHeartBeatTime;
    }


    public NodeBean() {
    }

    public NodeBean(int id, String ip, String host, String nodeName, int status) {
        this.id = id;
        this.ip = ip;
        this.host = host;
        this.nodeName = nodeName;
        this.status = status;
    }

    @Override
    public boolean equals(Object obj) {
        NodeBean nodeBean = (NodeBean) obj;
        return (ip + host + nodeName).equals(nodeBean.getIp() + nodeBean.getHost() + nodeBean.getNodeName());
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getJsonString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ip", ip);
        jsonObject.put("host", host);
        jsonObject.put("node_name", nodeName);
        jsonObject.put("id", id);
        jsonObject.put("status", status);
        return jsonObject.toJSONString();
    }

    public void getBeanFromString(String jsonStr) {
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        this.setId(jsonObject.getInteger("id"));
        this.setIp(jsonObject.getString("ip"));
        this.setHost(jsonObject.getString("host"));
        this.setNodeName(jsonObject.getString("node_name"));
        this.setStatus(jsonObject.getInteger("status"));
    }
}
