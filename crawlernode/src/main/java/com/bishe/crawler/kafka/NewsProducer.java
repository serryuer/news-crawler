package com.bishe.crawler.kafka;

import java.util.Properties;

import com.alibaba.fastjson.JSONObject;
import com.bishe.crawler.web.Page;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewsProducer extends KafkaComponent {

    private static final Logger logger = LoggerFactory.getLogger(NewsProducer.class);

    private static final String NEWS_TOPIC = "news_raw";

    private Producer<String, String> producer;


    public NewsProducer() {
        super();
    }

    @Override
    protected void init() {
        super.init();
        this.producer = new KafkaProducer<String, String>(props);
    }

    public void sendNewToKafka(Page page, int nodeID) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("charset", page.getCharset());
        jsonObject.put("content", page.getHtml());
        jsonObject.put("url", page.getUrl());
        jsonObject.put("tag", page.getTag());
        jsonObject.put("time", page.getCrawlTime().getTime());
        jsonObject.put("crawl_time", page.getCrawlTime().getTime());
        jsonObject.put("node_id", nodeID);
        if (producer == null) {
            init();
        }
        producer.send(new ProducerRecord<String, String>(NEWS_TOPIC, jsonObject.toJSONString()));
        logger.info("save news to kafka success");
    }

    public void sendMessage(String message) {
        for (int i = 0; i < 10; i++) {
            producer.send(new ProducerRecord<>("web_news", Integer.toString(i), message));
        }
    }

    public static void main(String[] args) {
        NewsProducer producer = new NewsProducer();
        producer.sendMessage("test");
    }

}
