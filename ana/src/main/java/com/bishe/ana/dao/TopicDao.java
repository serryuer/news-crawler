package com.bishe.ana.dao;

import com.bishe.ana.bean.Topic;
import com.bishe.crawler.dao.BaseDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TopicDao extends BaseDAO {

    private static final Logger logger = LoggerFactory.getLogger(TopicDao.class);


    public List<Topic> getAllTopics() {
        String sql = "SELECT name, create_time, last_update_time, is_valid, keyword, class FROM topic WHERE is_valid=1";
        PreparedStatement statement = null;
        try {
            if (connection == null || connection.isClosed()) {
                connection = DBConnection.getConnection();
            }
            statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            List<Topic> list = new ArrayList();
            while (resultSet.next()) {
                Topic topic = new Topic();
                topic.setName(resultSet.getString(1));
                topic.setCreateTime(resultSet.getTimestamp(2));
                topic.setLastUpdateTime(resultSet.getTimestamp(3));
                topic.setValid(resultSet.getInt(4) == 1);
                topic.setWordsByJsonString(resultSet.getString(5));
                topic.setClassID(resultSet.getInt(6));
                list.add(topic);
            }
            return list;
        } catch (SQLException e) {
            logger.error("get all node failed");
            e.printStackTrace();
        }
        return null;
    }

    public List<Topic> getTopicsByClass(int classID) {
        String sql = "SELECT name, create_time, last_update_time, is_valid, keyword, class FROM topic WHERE is_valid=1";
        PreparedStatement statement = null;
        try {
            if (connection == null || connection.isClosed()) {
                connection = DBConnection.getConnection();
            }
            statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            List<Topic> list = new ArrayList();
            while (resultSet.next()) {
                Topic topic = new Topic();
                topic.setName(resultSet.getString(1));
                topic.setCreateTime(resultSet.getTimestamp(2));
                topic.setLastUpdateTime(resultSet.getTimestamp(3));
                topic.setValid(resultSet.getInt(4) == 1);
                topic.setWordsByJsonString(resultSet.getString(5));
                topic.setClassID(resultSet.getInt(6));
                list.add(topic);
            }
            return list;
        } catch (SQLException e) {
            logger.error("get all node failed");
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateTopicStatus(Topic topic) {
        String sql = "update topic set is_valid=" + (topic.isValid() ? 1 : 0) + " where name=\"" + topic.getName() + "\"";
        boolean re = execute(sql);
        if (re) {
            logger.info("update topic success");
        } else {
            logger.info("update topic failed");
        }
        return re;
    }

    public boolean updateTopicWords(Topic topic) {
        String sql = "update topic set keyword=" + topic.getWordsJsonString() + " where name=\"" + topic.getName() + "\"";
        boolean re = execute(sql);
        if (re) {
            logger.info("update topic keyword success");
        } else {
            logger.info("update topic keyword failed");
        }
        return re;
    }

    public boolean deleteTopic(Topic topic) {
        String sql = "delete from topic where name=\"" + topic.getName() + "\"";
        boolean re = execute(sql);
        if (re) {
            logger.info("delete topic success");
        } else {
            logger.info("delete topic failed");
        }
        return re;
    }

    public boolean insertTopic(Topic topic) {
        String sql = "INSERT INTO topic(is_valid, name, keyword, create_time, last_update_time, class) VALUE(?,?,?,?,?, ?) ON DUPLICATE KEY UPDATE is_valid = 1";
        PreparedStatement statement = null;
        try {
            if (connection == null || connection.isClosed()) {
                connection = DBConnection.getConnection();
            }
            statement = connection.prepareStatement(sql);
            statement.setInt(1, topic.isValid() ? 1 : 0);
            statement.setString(2, topic.getName());
            statement.setString(3, topic.getWordsJsonString());
            statement.setTimestamp(4, topic.getCreateTime());
            statement.setTimestamp(5, topic.getLastUpdateTime());
            statement.setInt(6, topic.getClassID());
            int re = statement.executeUpdate();
            if (re == 1) {
                return true;
            } else {
//                logger.info("update none lines");
            }
        } catch (SQLException e) {
            logger.error("insert topic failed");
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateTopic(Topic topic) {
        String sql = "UPDATE topic SET keyword = ?, last_update_time = ?, is_valid = ? WHERE name = ?";
        PreparedStatement statement = null;
        try {
            if (connection == null || connection.isClosed()) {
                connection = DBConnection.getConnection();
            }
            statement = connection.prepareStatement(sql);
            statement.setString(1, topic.getWordsJsonString());
            statement.setTimestamp(2, topic.getLastUpdateTime());
            statement.setBoolean(3, topic.isValid());
            statement.setString(4, topic.getName());
            int re = statement.executeUpdate();
            if (re == 1) {
                return true;
            } else {
                logger.info("update none lines");
            }
        } catch (SQLException e) {
            logger.error("get all node failed");
            e.printStackTrace();
        }
        return false;
    }


    public static void main(String[] args) {
        Topic topic = new Topic();
        topic.setName("topic_test");
        topic.setValid(true);
        topic.setWordsByJsonString("[{\"word\":\"test\", \"value\":1.0}]");
        TopicDao topicDao = new TopicDao();
        topicDao.insertTopic(topic);
        topic.setValid(false);
        topicDao.updateTopicStatus(topic);
        topicDao.deleteTopic(topic);
    }


}
