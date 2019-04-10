package com.bishe.crawler.dao;

import com.bishe.crawler.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BaseDAO {

    private static final Logger logger = LoggerFactory.getLogger(SiteDAO.class);

    protected Connection connection;

    public BaseDAO() {
        connection = DBConnection.getConnection();
    }

    public void exit() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    protected boolean execute(String sql) {
        PreparedStatement statement = null;
        try {
            if (connection == null || connection.isClosed()) {
                connection = DBConnection.getConnection();
            }
            statement = connection.prepareStatement(sql);
            statement.execute();
//            connection.commit();
            return true;
        } catch (SQLException e) {
            logger.error("execute sql error");
            e.printStackTrace();
        }
        return false;
    }
}
