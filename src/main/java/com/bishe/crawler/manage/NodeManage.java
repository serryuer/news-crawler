package com.bishe.crawler.manage;

import com.alibaba.fastjson.JSONObject;
import com.bishe.crawler.dao.NodeDAO;
import com.bishe.crawler.jms.JMSReceiver;
import com.bishe.crawler.jms.JMSSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class NodeManage extends JMSReceiver implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(NodeManage.class);

    private ArrayList<NodeBean> nodes;

    private NodeDAO nodeDAO;

    private JMSSender initSender;

    public NodeManage(Connection con, String queue, boolean isTopic) {
        super(con, queue, isTopic);
    }

    public NodeManage(String queue, boolean isTopic) {
        super(queue, isTopic);
    }

    @Override
    protected void init(Connection connection, String queueName, boolean isTopic) {
        super.init(connection, queueName, isTopic);
        this.nodes = new ArrayList<>();
        this.nodeDAO = new NodeDAO();
        this.initSender = new JMSSender("id_init", true);
        startNodeStatusInitSchedule();
    }

    private void startNodeStatusInitSchedule() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            Logger logger = LoggerFactory.getLogger(this.getClass());

            @Override
            public void run() {
                logger.info("update all node status");
                //3分钟没有收到心跳认为dead
                nodes.forEach(node -> {
                    if (System.currentTimeMillis() - node.getLastHeartBeatTime() > (1000L * 60 * 10)) {
                        logger.info("node [" + node.getHost() + "/" + node.getIp() + "] is dead");
                        //重置为0
                        node.setStatus(0);
                    } else {
                        logger.info("node [" + node.getHost() + "/" + node.getIp() + "] is alive");
                    }
                });
            }
        }, 1000, 1000L * 60 * 3);
        this.setMessageListener(this);
    }


    @Override
    public void onMessage(Message message) {
        String heartBeat = "";
        try {
            heartBeat = ((TextMessage) message).getText();
        } catch (JMSException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = JSONObject.parseObject(heartBeat);
        NodeBean nodeBean = new NodeBean();
        nodeBean.setIp(jsonObject.getString("ip"));
        nodeBean.setHost(jsonObject.getString("host"));
        nodeBean.setNodeName(jsonObject.getString("thread_name"));
        logger.info("receive heart beat from [" + nodeBean.getIp() + "/" + nodeBean.getHost() + "/" + nodeBean.getNodeName() + "]");
        Long timestamp = jsonObject.getLong("timestamp");
        if (System.currentTimeMillis() - Long.valueOf(timestamp) > 1000L * 30) {
            logger.info("the heart beat info is out of time");
            return;
        }
        if (!nodes.contains(nodeBean)) {
            logger.info("add a new node");
            nodeBean.setStatus(1);
            nodeDAO.insertNode(nodeBean);
            nodeBean.setId(nodeDAO.getNodeID(nodeBean));
            nodes.add(nodeBean);
            logger.info("send node id initialize message to node");
            initSender.sendMessage(nodeBean.getJsonString());
        } else {
            for (NodeBean node : nodes) {
                if (node.equals(nodeBean)) {
                    if (node.getStatus() == 0) {
                        node.setStatus(1);
                        logger.info("send id info of node to node");
                        initSender.sendMessage(nodeBean.getJsonString());
                        logger.info("update the status of node");
                    }
                    node.setLastHeartBeatTime(System.currentTimeMillis());
                    break;
                }
            }
        }

    }
}
