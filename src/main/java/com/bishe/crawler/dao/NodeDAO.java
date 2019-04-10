package com.bishe.crawler.dao;

import com.bishe.crawler.manage.NodeBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NodeDAO extends BaseDAO {

    private static final Logger logger = LoggerFactory.getLogger(NodeDAO.class);

    public List<NodeBean> getAllNodes() {
        String sql = "SELECT id, node_name, host, ip, status  FROM node";
        PreparedStatement statement = null;
        try {
            if (connection == null || connection.isClosed()) {
                connection = DBConnection.getConnection();
            }
            statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            List<NodeBean> list = new ArrayList();
            while (resultSet.next()) {
                NodeBean nodeBean = new NodeBean();
                nodeBean.setStatus(resultSet.getInt(5));
                nodeBean.setNodeName(resultSet.getString(2));
                nodeBean.setId(resultSet.getInt(1));
                nodeBean.setHost(resultSet.getString(3));
                nodeBean.setIp(resultSet.getString(4));
                list.add(nodeBean);
            }
            return list;
        } catch (SQLException e) {
            logger.error("get all node failed");
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateNodeStatus(NodeBean nodeBean) {
        String sql = "update node set status=" + nodeBean.getStatus() + " where ip=\"" + nodeBean.getIp() + "\" and host=\""
                + nodeBean.getHost() + "\"  and id=" + nodeBean.getId() + " and node_name=\"" + nodeBean.getNodeName() + "\"";
        boolean re = execute(sql);
        if (re) {
            logger.info("update node success");
        } else {
            logger.info("update node failed");
        }
        return re;
    }

    public boolean insertNode(NodeBean nodeBean) {
        String sql = "insert into node(status, ip, host, node_name) value(" + nodeBean.getStatus() + " ,\"" + nodeBean.getIp() + "\" ,\""
                + nodeBean.getHost() + "\" , \"" + nodeBean.getNodeName() + "\")";
        boolean re = execute(sql);
        if (re) {
            logger.info("insert node success");
        } else {
            logger.info("insert node failed");
        }
        return re;
    }

    public int getNodeID(NodeBean nodeBean) {
        String sql = "select id from node where ip=\"" + nodeBean.getIp() + "\" and host=\""
                + nodeBean.getHost() + "\"  and node_name=\"" + nodeBean.getNodeName() + "\"";
        PreparedStatement statement = null;
        try {
            if (connection == null || connection.isClosed()) {
                connection = DBConnection.getConnection();
            }
            statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                logger.info("get the id of node[" + nodeBean.getHost() + "/" + nodeBean.getIp() + "] success, which is [" + resultSet.getInt(1) + "]");
                return resultSet.getInt(1);
            }
            return -1;
        } catch (SQLException e) {
            logger.error("get node id failed");
            e.printStackTrace();
        }
        return -1;
    }

    public static void main(String[] args) {
        NodeBean nodeBean = new NodeBean();
        nodeBean.setStatus(1);
        nodeBean.setNodeName("aa");
        nodeBean.setHost("aaa");
        nodeBean.setIp("aaaa");
        nodeBean.setId(1);
        NodeDAO dao = new NodeDAO();
        dao.insertNode(nodeBean);
    }

}
