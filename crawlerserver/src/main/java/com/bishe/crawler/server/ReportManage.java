package com.bishe.crawler.server;

import com.bishe.crawler.jms.JMSReceiver;

import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageListener;

public class ReportManage extends JMSReceiver implements MessageListener {


    public ReportManage(Connection con, String queue, boolean isTopic) {
        super(con, queue, isTopic);
    }

    public ReportManage(String queue, boolean isTopic) {
        super(queue, isTopic);
    }

    @Override
    public void onMessage(Message message) {

    }
}
