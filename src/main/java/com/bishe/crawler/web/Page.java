package com.bishe.crawler.web;

import com.alibaba.fastjson.JSONObject;
import com.bishe.crawler.util.CharsetDetector;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;

public class Page {

    private byte[] content;
    private String html;  //网页源码字符串
    private Document doc;//网页Dom文档
    private String charset;//字符编码
    private String url;//url路径
    private String contentType;// 内容类型
    private String tag;

    public Timestamp getCrawlTime() {
        return crawlTime;
    }

    public void setCrawlTime(Timestamp crawlTime) {
        this.crawlTime = crawlTime;
    }

    private Timestamp crawlTime;//抓取时间

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }


    public Page(byte[] content, String url, String contentType) {
        this.content = content;
        this.url = url;
        this.contentType = contentType;
    }

    public Page(String html, String url, String contentType) {
        this.html = html;
        this.url = url;
        this.contentType = contentType;
    }

    public String getCharset() {
        return charset;
    }

    public String getUrl() {
        return url;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getContent() {
        return content;
    }

    /**
     * 返回网页的源码字符串
     *
     * @return 网页的源码字符串
     */
    public String getHtml() {
        if (html != null) {
            return html;
        }
        if (content == null) {
            return null;
        }
        if (charset == null) {
            charset = CharsetDetector.guessEncoding(content); // 根据内容来猜测 字符编码
        }
        try {
            this.html = new String(content, charset);
            return html;
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /*
     *  得到文档
     * */
    public Document getDoc() {
        if (doc != null) {
            return doc;
        }
        try {
            this.doc = Jsoup.parse(getHtml(), url);
            return doc;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public boolean isNewsPage() {
        return true;
    }

    public String getJsonString(int nodeID) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("charset", this.getCharset());
        jsonObject.put("content", this.getHtml());
        jsonObject.put("url", this.getUrl());
        jsonObject.put("tag", this.getTag());
        jsonObject.put("time", this.getCrawlTime().getTime());
        jsonObject.put("crawl_time", this.getCrawlTime().getTime());
        jsonObject.put("node_id", nodeID);
        return jsonObject.toJSONString();
    }

}