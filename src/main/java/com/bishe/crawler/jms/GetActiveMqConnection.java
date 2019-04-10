package com.bishe.crawler.jms;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class GetActiveMqConnection extends MessageBussConnector {


    private static final Logger logger = LoggerFactory.getLogger(GetActiveMqConnection.class);

    static {
       logger.info("ActiveMQ工厂类");
        Properties pro = new Properties();
        try {
            pro.load(new FileInputStream("config/properties.ini"));
            String connectString = (String) pro.get("activemq.connectString");
            logger.info("ActiveMQAddress=" + connectString);
            address = connectString;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static ActiveMQConnection getConnection() {
        try {
            ActiveMQConnection connection = null;
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                    ActiveMQConnection.DEFAULT_USER,
                    ActiveMQConnection.DEFAULT_PASSWORD,
                    address);
            connection = (ActiveMQConnection) connectionFactory.createConnection();
            connection.start();
            return connection;
        } catch (Exception ex) {
            System.out.println("GetConnection Error");
            ex.printStackTrace();
            System.exit(-1);
            return null;
        }

    }

}
