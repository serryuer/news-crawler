package com.bishe.crawler.task;

import com.alibaba.fastjson.JSONObject;
import com.bishe.crawler.task.Task.TaskStatus;
import jdk.nashorn.internal.parser.JSONParser;

public class TaskConvert {

    public static String convertFromBeanToString(Task task) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("url", task.getUrl());
        jsonObject.put("host", task.getHost());
        jsonObject.put("weight", task.getWeight());
        jsonObject.put("status", task.getStatus().ordinal());
        jsonObject.put("tag", task.getTag());
        jsonObject.put("depth", String.valueOf(task.getDepth()));
        return jsonObject.toJSONString();
    }

    public static Task convertFromStringToBean(String taskStr) {
        JSONObject jsonObject = JSONObject.parseObject(taskStr);
        Task task = new Task();
        if (jsonObject.containsKey("weight")) {
            task.setWeight(jsonObject.getInteger("weight"));
        }
        if (jsonObject.containsKey("url")) {
            task.setUrl(jsonObject.getString("url"));
        } else {
            return null;
        }
        if (jsonObject.containsKey("host")) {
            task.setHost(jsonObject.getString("host"));
        }
        if (jsonObject.containsKey("tag")) {
            task.setTag(jsonObject.getString("tag"));
        }
        if (jsonObject.containsKey("depth")) {
            task.setDepth(Integer.valueOf(jsonObject.getString("depth")));
        }
        return task;
    }

}
