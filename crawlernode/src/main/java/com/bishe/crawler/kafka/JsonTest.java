package com.bishe.crawler.kafka;

import com.alibaba.fastjson.JSONObject;

public class JsonTest {

    public static void main(String[] args) {
        JSONObject object = new JSONObject();
        object.put("text", "\"{<html></html>\\test}");
        JSONObject object1 = JSONObject.parseObject(object.toJSONString());
        System.out.println(object.toJSONString());
        System.out.println(object1.getString("text"));
    }

}
