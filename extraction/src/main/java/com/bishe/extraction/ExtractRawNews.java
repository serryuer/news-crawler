package com.bishe.extraction;

import com.alibaba.fastjson.JSONObject;
import com.bishe.extraction.bean.WebNew;
import com.bishe.extraction.extraction.DataExtraction;
import com.gs.Classifier.BayesClassifier;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.*;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ExtractRawNews {

    private static final Logger logger = LoggerFactory.getLogger(ExtractRawNews.class);
    private MongoCollection rawCollection;
    private MongoCollection structureCollection;

    public ExtractRawNews() {
        init();
    }


    private void init() {
        Properties properties = new Properties();
        String ip = "127.0.0.1", port = "27017", databaseName = "bishe";
        try {
            properties.load(new FileReader("config/properties.ini"));
            port = properties.getProperty("mongo.port");
            ip = properties.getProperty("mongo.ip");
            databaseName = properties.getProperty("mongo.database");
        } catch (IOException e) {
            logger.error("load properties file error, use default config");
            e.printStackTrace();
        }
        MongoClientOptions.Builder options = MongoClientOptions.builder();
        options.socketKeepAlive(true);
        options.connectionsPerHost(500);
        options.socketTimeout(10000);
        options.threadsAllowedToBlockForConnectionMultiplier(1000);
        options.connectTimeout(100000);
//        MongoClient mongoClient = new MongoClient("45.76.76.48", options.build());
        MongoClient mongoClient = new MongoClient("127.0.0.1", options.build());
        MongoDatabase database = mongoClient.getDatabase("bishe");
        rawCollection = database.getCollection("news");
//        MongoClient client = new MongoClient("45.76.75.221", options.build());
//        MongoDatabase database1 = client.getDatabase("bishe");
        structureCollection = database.getCollection("structure_news");
    }


    public void extract() {

//        MongoCursor<Document> mongoCursor = rawCollection.find().skip(535 + 480).limit(100).iterator();
//        int i = 0;
//        int n = 1;
//        BayesClassifier bayesClassifier = BayesClassifier.getBayesClassifier();
//        while (true) {
//            if (!mongoCursor.hasNext()) {
//                mongoCursor.close();
//                mongoCursor = rawCollection.find().skip(n * 100 + 535 + 480).limit(100).iterator();
//                logger.info("get new cursor");
//                n++;
//                if (!mongoCursor.hasNext()) {
//                    break;
//                }
//            }
//            i++;
//            Document document = mongoCursor.next();
//            if (!document.getString("url").endsWith("html")) {
//                continue;
//            }
//            logger.info("etract [" + i + "] : [" + document.getString("url") + "]");
//            WebNew webNew = extraction.extractStructureNewsInfo(document.getString("content"), document.getString("url"), "");
//
//            if (webNew != null) {
//                logger.info("save structure info to mongodb");
//                Document structureDocument = new Document();
//                structureDocument.append("content", webNew.getContent())
//                        .append("title", webNew.getTitle())
//                        .append("author", webNew.getAuthor())
//                        .append("time_str", webNew.getTimeStr())
//                        .append("url", document.getString("url"))
//                        .append("tag", document.getString("tag"))
////                        .append("node_id", document.getString("node_id"))
//                        .append("charset", document.getString("charset"))
//                        .append("crawl_time", document.getLong("crawl_time"))
//                        .append("class", bayesClassifier.classify(webNew.getContent()));
//                structureCollection.insertOne(structureDocument);
//                logger.info("title : " + webNew.getTitle());
//                logger.info("author : " + webNew.getAuthor());
//                logger.info("time : " + webNew.getTimeStr());
//                logger.info("tag : " + document.getString("tag"));
//            } else {
//                continue;
//            }
//        }
    }

    private static Properties initConfig() {
        Properties properties = new Properties();
        //指定流处理应用的id
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "stream-extract");
        //指定地址
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "PLAINTEXT://45.76.75.221:9092,PLAINTEXT://45.76.76.48:9092,PLAINTEXT://45.76.79.97:9092");
        //指定序列化与反序列化的类
        properties.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        properties.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        //指定自动偏移量提交策略
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return properties;
    }

    public static void main(String[] args) {
        KStreamBuilder builder = new KStreamBuilder();
        //构建KStream日志流
        final Serializer<String> stringSerializer = new StringSerializer();
        final Deserializer<String> stringDeserializer = new StringDeserializer();

        final Serdes.StringSerde stringSerde = new Serdes.StringSerde();

        KStream<String, String> kStream = builder.stream("web_news");

        KStream<String, String> extract = kStream.map((key, value) -> extractInfo(key, value)).filter((key, value) -> value != null);
        extract.to(stringSerde, stringSerde, "structure_news");
        KafkaStreams streams = new KafkaStreams(builder, initConfig());
        streams.start();
    }

    private final static DataExtraction dataExtaction = new DataExtraction();
    private final static BayesClassifier bayesClassifier = BayesClassifier.getBayesClassifier();


    private static int i = 0;

    private static <V1, K1> KeyValue<String, String> extractInfo(String key, String value) {
        JSONObject jsonObject = null;
        try {
            jsonObject = JSONObject.parseObject(value);
        } catch (Exception e) {
            logger.info("parse json string failed : " + value);
            return new KeyValue<>(key, null);
        }

        logger.info("extract [" + i++ + "], [" + jsonObject.getString("url") + "]");
        WebNew webNew = dataExtaction.extractStructureNewsInfo(jsonObject.getString("content"), jsonObject.getString("url"), jsonObject.getString("tag"));
        if (webNew == null) {
            return new KeyValue<>(key, null);
        }
        webNew.setUrl(jsonObject.getString("url"));
        webNew.setClassID(bayesClassifier.classify(webNew.getContent()));
        webNew.setTag(jsonObject.getString("tag"));
        return new KeyValue<String, String>(key, webNew.toJsonString());
    }


}
