package com.bishe.crawler.jms;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQSession;

import javax.jms.*;

public abstract class JMSComponent {
    protected Session session;
    protected Destination destination;
    protected String currentQueue;
    protected boolean iSTopic;
    protected ActiveMQConnection con;


    protected JMSComponent(Connection connection, String queue, boolean isTopic) {
        init(connection, queue, isTopic);
    }

    protected JMSComponent(String queue, boolean iSTopic) {
        con = GetActiveMqConnection.getConnection();
        init(con, queue, iSTopic);
    }

    protected void init(Connection connection, String queueName, boolean isTopic) {
        this.currentQueue = queueName;
        this.iSTopic = isTopic;
        try {
            con = (ActiveMQConnection) connection;
            session = con.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
            if (isTopic) {
                destination = session.createTopic(this.currentQueue);
            } else {
                destination = session.createQueue(this.currentQueue);
            }
            connection.start();
        } catch (JMSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void exit() {
        try {
            if (session != null) {
                session.close();
            }
            if (con != null) {
                con.close();
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    //check the connection state
    protected boolean isConnectable() {
        ActiveMQSession mySession = (ActiveMQSession) session;
        return mySession.isClosed();
    }


}
