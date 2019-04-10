package com.bishe.ana;

import com.alibaba.fastjson.JSONObject;
import com.bishe.ana.bean.New;
import com.bishe.ana.dao.NewDao;
import com.bishe.crawler.kafka.CustomKafkaConsumer;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class AnalyseNews {

    private static final Logger logger = LoggerFactory.getLogger(AnalyseNews.class);
    private MongoCollection newsCollection;

    public AnalyseNews() {
        init();
    }


    private void init() {
        MongoClientOptions.Builder options = MongoClientOptions.builder();
        options.socketKeepAlive(true);
        options.connectionsPerHost(500);
        options.socketTimeout(10000);
        options.threadsAllowedToBlockForConnectionMultiplier(1000);
        options.connectTimeout(100000);
        MongoClient mongoClient = new MongoClient("127.0.0.1", options.build());
        MongoDatabase database = mongoClient.getDatabase("bishe");
        newsCollection = database.getCollection("structure_news");
    }

    public void analyse() {
        NewsANA newsANA = new NewsANA();
        NewDao newDao = new NewDao();
        FindIterable<Document> findIterable = newsCollection.find();
        MongoCursor<Document> mongoCursor = findIterable.skip(1583).limit(100).iterator();
        int i = 0;
        int n = 1;
        while (true) {
            if (!mongoCursor.hasNext()) {
                mongoCursor = findIterable.skip(n * 100 + 1583).limit(100).iterator();
                n++;
                if (!mongoCursor.hasNext()) {
                    break;
                }
            }
            i++;
            Document document = mongoCursor.next();
            logger.info("analyse [" + i + "] : [" + document.getString("url") + "]");
            New ne = new New();
            ne.setTitle(document.getString("title"));
            ne.setContext(document.getString("content"));
            ne.setTag(document.getString("tag"));
            ne.setAuthor(document.getString("author"));
            ne.setUrl(document.getString("url"));
            ne.setTime(document.getString("time_str"));
            if (document.getInteger("class") == null) {
                ne.setClassID(9);
            } else {
                ne.setClassID(document.getInteger("class"));
            }
            if (newsANA.analyseNews(ne)) {
                newDao.insertNew(ne);
            }
        }
    }

    public static void main(String[] args) {


        NewsANA newsANA = new NewsANA();
        NewDao newDao = new NewDao();

        CustomKafkaConsumer consumer = new CustomKafkaConsumer("news_ana");
        consumer.subscribe(Arrays.asList("structure_news"));

        int i = 0;

        NewsFilter newsFilter = new NewsFilter();
        try {
            while (true) {
                ConsumerRecords<String, String> record = consumer.getConsumer().poll(1000);
                for (ConsumerRecord<String, String> consumerRecord : record) {
                    JSONObject jsonObject = null;
                    if (consumerRecord.value() == null) {
                        continue;
                    }
                    try {
                        jsonObject = JSONObject.parseObject(consumerRecord.value());
                    } catch (Exception e) {
                        logger.info("parse json string failed : " + consumerRecord.value());
                    }
                    logger.info("analyse [" + i + "] : [" + jsonObject.getString("url") + "]");
                    String str = jsonObject.getString("title");
                    if (jsonObject.getString("url").length() > 15) {
                        str += jsonObject.getString("url").substring(0, 10);
                    }
                    if (newsFilter.isContainTitle(str)) {
                        logger.info("the news is repeat : [" + jsonObject.getString("title") + "]/[" + jsonObject.getString("url") + "]");
                        continue;
                    }
                    New ne = new New();
                    ne.setTitle(jsonObject.getString("title"));
                    ne.setContext(jsonObject.getString("content"));
                    ne.setTag(jsonObject.getString("tag"));
                    ne.setAuthor(jsonObject.getString("author"));
                    ne.setUrl(jsonObject.getString("url"));
                    ne.setTime(jsonObject.getString("time_str"));
                    if (jsonObject.getInteger("class") == null) {
                        ne.setClassID(9);
                    } else {
                        ne.setClassID(jsonObject.getInteger("class"));
                    }
                    if (newsANA.analyseNews(ne)) {
                        newDao.insertNew(ne);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
