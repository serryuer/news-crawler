package com.bishe.crawler.client;

import com.alibaba.fastjson.JSONObject;
import com.bishe.crawler.jms.JMSSender;
import com.bishe.crawler.util.PCUtil;

import java.sql.Timestamp;

public class HeartBeat {

    private String ip;
    private String host;
    private String threadName;

    private Timestamp timeStamp;
    private JMSSender sender;

    public HeartBeat() {
        ip = PCUtil.getLocalIP();
        host = PCUtil.getLocalHostName();
        threadName = Thread.currentThread().getName();
        sender = new JMSSender("heart_beat", false);
    }

    private String getHeartBeatJSONString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ip", ip);
        jsonObject.put("host", host);
        jsonObject.put("thread_name", threadName);
        jsonObject.put("timestamp", timeStamp.getTime());
        return jsonObject.toJSONString();
    }

    public void sendHeartBeat() {
        timeStamp = new Timestamp(System.currentTimeMillis());
        sender.sendMessage(getHeartBeatJSONString());
    }

    public static void main(String[] args) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println(System.currentTimeMillis());
        System.out.println(timestamp.getTime());
        System.out.println(timestamp.toString());
    }

}
