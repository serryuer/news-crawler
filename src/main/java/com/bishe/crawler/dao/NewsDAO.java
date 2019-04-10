package com.bishe.crawler.dao;

import com.bishe.crawler.web.Page;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class NewsDAO {

    private static final Logger logger = LoggerFactory.getLogger(NewsDAO.class);

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection collection;

    private static NewsDAO newsDAO;

    private NewsDAO() {
        init();
    }

    private void init() {
        Properties properties = new Properties();
        String ip = "127.0.0.1", port = "27017", databaseName = "bishe";
        try {
            properties.load(new FileReader("config/properties.ini"));
            ip = properties.getProperty("mongo.ip");
            port = properties.getProperty("mongo.port");
            databaseName = properties.getProperty("mongo.database");
        } catch (IOException e) {
            logger.error("load properties file error, use default config");
            e.printStackTrace();
        }
        mongoClient = new MongoClient(ip, Integer.parseInt(port));
        database = mongoClient.getDatabase(databaseName);
        collection = database.getCollection("news");
    }

    public static NewsDAO getInstance() {
        if (newsDAO == null) {
            newsDAO = new NewsDAO();
        }
        return newsDAO;
    }

    public boolean savePage(Page page, int nodeID) {
        if (page == null) {
            return false;
        }
        if (collection == null) {
            init();
        }
        Document document = new Document();
        document.append("content", page.getHtml());
        document.append("url", page.getUrl()).append("charset", page.getCharset())
                .append("tag", page.getTag()).append("crawl_time", page.getCrawlTime().getTime())
                .append("node_id", nodeID);
        collection.insertOne(document);
        return true;
    }
}
