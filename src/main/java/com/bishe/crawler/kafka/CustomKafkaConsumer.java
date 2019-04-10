package com.bishe.crawler.kafka;

import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.List;
import java.util.Properties;

public class CustomKafkaConsumer {

    private String groupID;

    public KafkaConsumer<String, String> getConsumer() {
        return consumer;
    }

    public void setConsumer(KafkaConsumer<String, String> consumer) {
        this.consumer = consumer;
    }

    private KafkaConsumer<String, String> consumer;

    public CustomKafkaConsumer(String groupID) {
        this.groupID = groupID;
        consumer = new KafkaConsumer<String, String>(initProperties());
    }


    private Properties initProperties() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "PLAINTEXT://45.76.75.221:9092,PLAINTEXT://45.76.76.48:9092,PLAINTEXT://45.76.79.97:9092");
        props.put("group.id", this.groupID);
        //如果value合法，则自动提交偏移量
        props.put("enable.auto.commit", "true");
        //设置多久一次更新被消费消息的偏移量
        props.put("auto.commit.interval.ms", "1000");
        //设置会话响应的时间，超过这个时间kafka可以选择放弃消费或者消费下一条消息
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return props;
    }

    public void subscribe(List<String> web_news) {
        consumer.subscribe(web_news);
    }
}
