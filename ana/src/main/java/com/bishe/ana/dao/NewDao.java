package com.bishe.ana.dao;

import com.bishe.ana.bean.New;
import com.bishe.crawler.dao.BaseDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class NewDao extends BaseDAO {

    private static final Logger logger = LoggerFactory.getLogger(NewDao.class);


    public boolean insertNew(New n) {
        String sql = "INSERT INTO topic_news(title, author, content, publish_time, abst, keyword, topic, tag, url) VALUE (?,?,?,?,?,?,?,?,?)";
        PreparedStatement statement = null;
        try {
            if (connection == null || connection.isClosed()) {
                connection = DBConnection.getConnection();
            }
            statement = connection.prepareStatement(sql);
            statement.setString(1, n.getTitle());
            statement.setString(2, n.getAuthor());
            statement.setString(3, n.getContext());
            statement.setTimestamp(4, new Timestamp(n.getTimes()));
            statement.setString(5, n.getAbst());
            statement.setString(6, n.getKeywords().toString());
            statement.setString(7, n.getTopic());
            statement.setString(8, n.getTag());
            statement.setString(9, n.getUrl());

            boolean re = statement.execute();
            if (re) {
                return true;
            } else {
//                logger.info("update none lines");
            }
        } catch (SQLException e) {
            logger.error("insert new failed");
            e.printStackTrace();
        }
        return false;
    }

    public int getNewsCountByTopic(String topic) {
        String sql = "SELECT count(*) FROM topic_news WHERE topic = ?";
        PreparedStatement statement = null;
        try {
            if (connection == null || connection.isClosed()) {
                connection = DBConnection.getConnection();
            }
            statement = connection.prepareStatement(sql);
            statement.setString(1, topic);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("insert new failed");
            e.printStackTrace();
        }
        return 0;
    }

    public boolean deleteNewsByTopic(String topic) {
        String sql = "DELETE FROM topic_news WHERE topic = ?";
        PreparedStatement statement = null;
        try {
            if (connection == null || connection.isClosed()) {
                connection = DBConnection.getConnection();
            }
            statement = connection.prepareStatement(sql);
            statement.setString(1, topic);
            boolean resultSet = statement.execute();
            return true;
        } catch (SQLException e) {
            logger.error("insert new failed");
            e.printStackTrace();
        }
        return false;
    }


    public static void main(String[] args) {

    }


}
