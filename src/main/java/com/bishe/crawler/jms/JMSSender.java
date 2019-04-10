package com.bishe.crawler.jms;


import com.alibaba.fastjson.JSONObject;
import com.bishe.crawler.task.Task;
import com.bishe.crawler.task.TaskConvert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

public class JMSSender extends JMSComponent {

    private static final Logger logger = LoggerFactory.getLogger(JMSSender.class);

    private MessageProducer producer;

    public JMSSender(Connection connection, String queue, boolean isTopic) {
        super(connection, queue, isTopic);
    }

    public JMSSender(String queue, boolean isTopic) {
        super(queue, isTopic);
    }

    @Override
    protected void init(Connection connection, String queueName, boolean isTopic) {
        super.init(connection, queueName, isTopic);
        try {
            producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        } catch (JMSException e) {
            logger.error("producer create failed");
            e.printStackTrace();
        }
    }

    public boolean sendMessage(String t) {
        while (isConnectable()) {
            logger.error("session connect failed，retry after one seconds");
            init(con, currentQueue, iSTopic);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if ((session != null) && (producer != null)) {
            TextMessage message;
            try {
                message = session.createTextMessage(t);
                producer.send(message);
//                session.commit();
            } catch (JMSException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            logger.error("connect failed");
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        while (true) {
            JMSSender sender = new JMSSender(GetActiveMqConnection.getConnection(), "url", false);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("link", "https://www.sina.com.cn");
            jsonObject.put("tag", "sina");
            sender.sendMessage(jsonObject.toJSONString());
            System.out.println("send success");
            try {
                Thread.sleep(1000L * 10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}