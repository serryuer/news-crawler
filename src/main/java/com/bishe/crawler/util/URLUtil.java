package com.bishe.crawler.util;

public class URLUtil {

    public static String getHost(String url) {
        if (url.startsWith("http")) {
            url = url.substring(url.indexOf("//") + 2);
        } else {
            return null;
        }
        if (url.contains("/")) {
            url = url.substring(0, url.indexOf("/"));
        }
        if (url.trim().equalsIgnoreCase("")) {
            return null;
        }
        return url;
    }

    public static String getPath(String url) {
        return url;
    }

    public static boolean isLegalURL(String url) {
        if (url == null || url.trim().equalsIgnoreCase("") ||
                !url.contains("http")) {
            return false;
        }
        if (!url.endsWith("html")) {
            return false;
        }
        return true;
    }

    public static boolean isSameSite(String domain1, String domain2) {
        domain1 = domain1.replaceAll("http://", "");
        domain1 = domain1.replaceAll("https://", "");
        domain1 = domain1.replaceAll("www.", "");
        domain1 = domain1.replaceAll(".com", "");
        domain1 = domain1.replaceAll(".xyz", "");
        domain1 = domain1.replaceAll(".net", "");
        domain1 = domain1.replaceAll(".tech", "");
        domain1 = domain1.replaceAll(".org", "");
        domain1 = domain1.replaceAll(".gov", "");
        domain1 = domain1.replaceAll(".edu", "");
        domain1 = domain1.replaceAll(".ink", "");
        domain1 = domain1.replaceAll(".com.cn", "");
        domain1 = domain1.replaceAll(".gov.cn", "");
        domain1 = domain1.replaceAll(".org.cn", "");
        domain1 = domain1.replaceAll(".net.cn", "");
        domain1 = domain1.replaceAll(".cn", "");
        domain1 = domain1.replaceAll(".tv", "");
        domain1 = domain1.replaceAll(".biz", "");
        domain2 = domain2.replaceAll("http://", "");
        domain2 = domain2.replaceAll("https://", "");
        domain2 = domain2.replaceAll("www.", "");
        domain2 = domain2.replaceAll(".com", "");
        domain2 = domain2.replaceAll(".xyz", "");
        domain2 = domain2.replaceAll(".net", "");
        domain2 = domain2.replaceAll(".tech", "");
        domain2 = domain2.replaceAll(".org", "");
        domain2 = domain2.replaceAll(".gov", "");
        domain2 = domain2.replaceAll(".edu", "");
        domain2 = domain2.replaceAll(".ink", "");
        domain2 = domain2.replaceAll(".com.cn", "");
        domain2 = domain2.replaceAll(".gov.cn", "");
        domain2 = domain2.replaceAll(".org.cn", "");
        domain2 = domain2.replaceAll(".net.cn", "");
        domain2 = domain2.replaceAll(".cn", "");
        domain2 = domain2.replaceAll(".tv", "");
        domain2 = domain2.replaceAll(".biz", "");
        String[] domains1 = domain1.split("\\.");
        String[] domains2 = domain2.split("\\.");
        if (domains1.length == 0 || domains2.length == 0) {
            return false;
        }
        for (String s : domains1) {
            for (String s1 : domains2) {
                if (s.trim().equalsIgnoreCase(s1.trim()) && s1.length() >= 2) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String encodeURL(String url) {
        String encodeURL = url.replaceAll("\\+", "%2B");
        encodeURL = encodeURL.replaceAll(" ", "%20");
        encodeURL = encodeURL.replaceAll("/+", "%2F");
        encodeURL = encodeURL.replaceAll("\\?", "%3F");
        encodeURL = encodeURL.replaceAll("%", "%25");
        encodeURL = encodeURL.replaceAll("#", "%23");
        encodeURL = encodeURL.replaceAll("&", "%26");
        encodeURL = encodeURL.replaceAll("=", "%3D");
        return encodeURL;
    }

    public static void main(String[] args) {
        System.out.println(URLUtil.getHost("http://news.sina.com.cn/c/xl/2018-05-16/doc-iharvfht9465254.shtml"));
    }
}
