package com.bishe.crawler.url;

import com.alibaba.fastjson.JSONObject;
import com.bishe.crawler.jms.JMSReceiver;
import com.bishe.crawler.jms.JMSSender;
import com.bishe.crawler.jms.MessageBusNames;
import com.bishe.crawler.task.Task;
import com.bishe.crawler.task.TaskConvert;
import com.bishe.crawler.util.URLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

public class URLReceiver extends JMSReceiver implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(URLReceiver.class);

    private JMSSender urlSender;


    private URLFilter urlFilter;

    public URLReceiver(Connection con, String queue, boolean isTopic) {
        super(con, queue, isTopic);
    }

    public URLReceiver(String queue, boolean isTopic) {
        super(queue, isTopic);
    }

    @Override
    protected void init(Connection connection, String queueName, boolean isTopic) {
        super.init(connection, queueName, isTopic);
        this.urlSender = new JMSSender(MessageBusNames.Task, false);
        this.urlFilter = new URLFilter();
        this.setMessageListener(this);
    }

    public URLFilter getUrlFilter() {
        return urlFilter;
    }

    public void setUrlFilter(URLFilter urlFilter) {
        this.urlFilter = urlFilter;
    }


    @Override
    public void onMessage(Message message) {
        String linkeBean = "";
        try {
            linkeBean = ((TextMessage) message).getText();
        } catch (JMSException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = JSONObject.parseObject(linkeBean);
        String url = jsonObject.getString("link");
        String tag = jsonObject.getString("tag");
        int depth = Integer.valueOf(jsonObject.getString("depth"));
        if (url.trim().equalsIgnoreCase("")) {
            return;
        }
        if (!url.endsWith("html") && !url.endsWith("htm")) {
            return;
        }
        //检测url是否重复
        if (!urlFilter.isContainUrl(url, tag)) {
            //不重复的Url作为任务发送到JMS
            logger.info("add url [" + url + "] to task queue");
            Task task = new Task();
            task.setHost(URLUtil.getHost(linkeBean));
            task.setUrl(url);
            task.setStatus(Task.TaskStatus.Created);
            task.setWeight(1);
            task.setTag(tag);
            task.setDepth(depth + 1);
            urlSender.sendMessage(TaskConvert.convertFromBeanToString(task));
        } else {
            logger.info("the url is repeated");
        }
    }
}
