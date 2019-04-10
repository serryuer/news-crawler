package com.bishe.crawler.kafka;

import org.apache.commons.codec.StringEncoder;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class KafkaComponent {

    private static final Logger logger = LoggerFactory.getLogger(KafkaComponent.class);

    protected Properties props;

    public KafkaComponent() {
        init();
    }


    protected void init() {
        props = new Properties();
        props.put("metadata.broker.list", "45.76.75.221:9092,45.76.76.48:9092,45.76.79.97:9092，45.63.51.6:9092");
        props.put("bootstrap.servers", "PLAINTEXT://45.76.75.221:9092,PLAINTEXT://45.76.76.48:9092,PLAINTEXT://45.76.79.97:9092");
        //The "all" setting we have specified will result in blocking on the full commit of the record, the slowest but most durable setting.
        //“所有”设置将导致记录的完整提交阻塞，最慢的，但最持久的设置。
        props.put("group.id", "news");
        props.put("group.name", "1");
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
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    }


}
