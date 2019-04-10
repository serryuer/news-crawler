package com.bishe.crawler.kafka;

import com.bishe.crawler.kafka.KafkaComponent;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

public class KafkaConsumerTest {

    public static void main(String[] args) {
        Properties properties = initProperties();
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);

        consumer.subscribe(Arrays.asList("structure_news_test"));

        try {
            while (true) {
                ConsumerRecords<String, String> record = consumer.poll(1000);
                for (ConsumerRecord<String, String> consumerRecord : record) {
                    System.out.println(consumerRecord.key());
                    System.out.println(consumerRecord.value());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static Properties initProperties() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "PLAINTEXT://45.76.75.221:9092,PLAINTEXT://45.76.76.48:9092,PLAINTEXT://45.76.79.97:9092");
        props.put("group.id", "test");
        props.put("cloient.id", "test");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
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
        return props;
    }


}
