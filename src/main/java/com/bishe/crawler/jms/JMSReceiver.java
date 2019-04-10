package com.bishe.crawler.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

public class JMSReceiver extends JMSComponent {

    private static final Logger logger = LoggerFactory.getLogger(JMSReceiver.class);

    private MessageConsumer consumer;

    public JMSReceiver(Connection con,
                       String queue,
                       boolean isTopic) {
        super(con, queue, isTopic);
    }

    public JMSReceiver(String queue, boolean isTopic) {
        super(queue, isTopic);
    }

    public void setMessageListener(MessageListener listener) {
        try {
            this.consumer.setMessageListener(listener);
        } catch (JMSException e) {
            logger.error("set listener failed");
            e.printStackTrace();
        }
    }


    @Override
    protected void init(Connection connection, String queueName, boolean isTopic) {
        super.init(connection, queueName, isTopic);
        try {
            consumer = session.createConsumer(destination);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public String receiveMessage() {
        try {
            Message msg = consumer.receive();
            if (msg == null) {
                return null;
            }
            if (msg instanceof TextMessage) {
                TextMessage txt = (TextMessage) msg;
                return txt.getText();
            } else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void main(String[] args) {
        JMSReceiver jmsReceiver = new JMSReceiver("report", false);
        System.out.println(jmsReceiver.receiveMessage());
//        jmsReceiver.setMessageListener(new MessageListener() {
//            @Override
//            public void onMessage(Message message) {
//                try {
//                    if (message instanceof TextMessage) {
//                        TextMessage txt = (TextMessage) message;
//                        System.out.println(txt.getText());
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        while (true){
//            System.out.println("test");
//            try {
//                Thread.sleep(1000*3);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }
}