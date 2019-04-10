package com.bishe.crawler.web;

import com.bishe.crawler.util.CharsetDetector;
import com.bishe.crawler.util.URLUtil;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;

public class RequestAndResponseTool {

    private static final Logger logger = LoggerFactory.getLogger(RequestAndResponseTool.class);


    public static Page sendRequstAndGetResponse(String url) {
        Page page = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(5000).setConnectionRequestTimeout(5000).build();
        //创建httpget实例
        HttpGet httpGet = null;  //系統有限制
        URL url1 = null;
        if (url.contains("?")) {
            String path = url.substring(0, url.indexOf('?'));
            String query = url.substring(url.indexOf('?') + 1, url.length());
            try {
                URI uri = new URI(null, null, path, query, null);
                httpGet = new HttpGet(uri);
            } catch (URISyntaxException e) {
                logger.error("url encode failed");
                e.printStackTrace();
            }
        } else if (url.contains("#")) {
            return null;
        } else {
            try {
                httpGet = new HttpGet(url);
            } catch (Exception e) {
                return null;
            }
        }
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36");
        httpGet.addHeader("Accept", "text/html");
        httpGet.addHeader("Accept-Charset", "gb2312,utf-8");
        httpGet.addHeader("Accept-Encoding", "gzip");
        httpGet.addHeader("Accept-Language", "zh-cn,zh,en-US,en");

        httpGet.setConfig(requestConfig);

        //执行http get 请求
        CloseableHttpResponse response = null;
        boolean result = false;
        try {
            logger.info("try to execute http get");
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                logger.error("request failed");
                return null;
            }
            logger.info("request success");
            String contentType = response.getFirstHeader("Content-Type").getValue();
            if (!contentType.contains("text") && !contentType.contains("html")) {
                logger.info("no expected content type [" + contentType + "]");
                return null;
            }
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                logger.info("entity is null");
                return null;
            }
            logger.info(entity.getContentType().getValue());
            logger.info("get the content");
            String content = EntityUtils.toString(entity, PageEncoding.getEncodingByContentUrl(url));
            logger.info("get the content success");
            page = new Page(content, url, contentType); //封装成为页面
            result = true;
        } catch (IOException e) {
            if (response != null) {
                try {
                    EntityUtils.toString(response.getEntity(), "utf-8");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            logger.error("request [" + url + "] failed");
//            e.printStackTrace();
        } catch (Exception e) {
            logger.error("request [" + url + "] failed");
        } finally {
            try {
//                String content = EntityUtils.toString(response.getEntity());// 用string接收响应实体
                EntityUtils.consume(response.getEntity());// 消耗响应实体
                if (response != null) {
                    response.close();
                }
                if (httpGet != null) {
                    httpGet.releaseConnection();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (result) {
            page.setCrawlTime(new Timestamp(System.currentTimeMillis()));
        } else {
            return null;
        }
        return page;
    }
}
