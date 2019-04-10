package com.bishe.crawler.client;

import com.alibaba.fastjson.JSONObject;
import com.bishe.crawler.jms.JMSSender;
import com.bishe.crawler.util.PCUtil;

import java.sql.Timestamp;

public class Reporter {
    private JMSSender sender;

    public Reporter() {
        sender = new JMSSender("report", false);
    }

    private String getReportString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("crawl_total_num", CrawlerNode.crawlTotalNum);
        jsonObject.put("ip", PCUtil.getLocalIP());
        jsonObject.put("host", PCUtil.getLocalHostName());
        double during = System.currentTimeMillis() - CrawlerNode.programStartTime / (1000L * 60 * 60);
        if (during >= CrawlerNode.programRunHours) {
            jsonObject.put("crawl_this_hour", CrawlerNode.crawlThisHour);
            CrawlerNode.crawlThisHour = 0;
            CrawlerNode.programRunHours++;
        }
        return jsonObject.toJSONString();
    }

    public void sendReport() {
        sender.sendMessage(getReportString());
    }

    public static void main(String[] args) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println(System.currentTimeMillis());
        System.out.println(timestamp.getTime());
        System.out.println(timestamp.toString());
    }

}

