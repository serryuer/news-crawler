package com.bishe.crawler.task;

import com.bishe.crawler.jms.GetActiveMqConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

public class TaskDetect {

    private static Logger logger = LoggerFactory.getLogger(TaskDetect.class);

    private Session session;
    private MessageProducer producer;
    private Message message;
    private MessageConsumer consumer;
    private Queue queue;

    public TaskDetect() {
        Connection connection = GetActiveMqConnection.getConnection();
        try {
            session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
            Queue replyTo = session.createTemporaryQueue();
            consumer = session.createConsumer(replyTo);
            message = session.createMessage();
            message.setJMSReplyTo(replyTo);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }


    public long getCount(String queueName) {
        queueName = "ActiveMQ.Statistics.Destination." + queueName;
        try {
            queue = session.createQueue(queueName);
            producer = session.createProducer(null);
            producer.send(queue, message);
            MapMessage reply = (MapMessage) consumer.receive(5000);
            long size = reply.getLong("size");
            logger.debug(queueName + "的大小(" + size + ")");
            return reply.getLong("size");
        } catch (JMSException ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    public static void main(String[] args) {
        TaskDetect taskDetect = new TaskDetect();
        taskDetect.getCount("task");
    }


}
