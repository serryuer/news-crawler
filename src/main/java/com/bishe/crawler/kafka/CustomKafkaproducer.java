package com.bishe.crawler.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class CustomKafkaproducer {


    private Producer<String, String> producer;
    private String topic;

    public CustomKafkaproducer(String topic) {
        this();
        this.topic = topic;
    }


    public CustomKafkaproducer() {
        producer = new KafkaProducer<String, String>(initProperties());
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }


    private Properties initProperties() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "PLAINTEXT://45.76.75.221:9092,PLAINTEXT://45.76.76.48:9092,PLAINTEXT://45.76.79.97:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("acks", "all");
        //如果请求失败，生产者也会自动重试，即使设置成０ the producer can automatically retry.
        props.put("retries", 0);

        //The producer maintains buffers of unsent records for each partition.
        props.put("batch.size", 16384);
        //默认立即发送，这里这是延时毫秒数
        props.put("linger.ms", 1);
        //生产者缓冲大小，当缓冲区耗尽后，额外的发送调用将被阻塞。时间超过max.block.ms将抛出TimeoutException
        props.put("buffer.memory", 33554432);
        //The key.serializer and value.serializer instruct how to turn the key and value objects the user provides with their ProducerRecord into bytes.
        return props;
    }

    public void sendMessage(String message) {
        producer.send(new ProducerRecord<>("web_news", message));
    }

    public static void main(String[] args) {
        CustomKafkaproducer customKafkaproducer = new CustomKafkaproducer("web_news");
        for (int i = 0; i < 1000; i++) {
            customKafkaproducer.sendMessage(Integer.toString(i));
        }

    }


}


