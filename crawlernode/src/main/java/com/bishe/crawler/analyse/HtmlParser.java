package com.bishe.crawler.analyse;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class HtmlParser {

    private static final double THRESHOLD = 0.01;

    public static boolean isWebNewPage(String html) {
        org.jsoup.nodes.Document document = Jsoup.parse(html);
        double length = html.length();
        double textLength = 0;
        Elements elements = document.getElementsByTag("p");
        for (Element element : elements) {
            textLength += element.text().length();
        }
        double ratio = textLength / length;
        return ratio > THRESHOLD;
    }

    public static void main(String[] args) {
        MongoClient mongoClient = new MongoClient("127.0.0.1", Integer.parseInt("27017"));
        MongoDatabase database = mongoClient.getDatabase("bishe");
        MongoCollection collection = database.getCollection("news");
        FindIterable<Document> findIterable = collection.find();
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        while (mongoCursor.hasNext()) {
            Document document = mongoCursor.next();
            System.out.println(document.getString("url"));
            HtmlParser.isWebNewPage(document.getString("content"));
        }
    }
}
