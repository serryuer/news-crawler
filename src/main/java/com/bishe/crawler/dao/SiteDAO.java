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

public class SiteDAO extends BaseDAO {

    private static final Logger logger = LoggerFactory.getLogger(SiteDAO.class);

    public SiteDAO() {
        super();
    }

    public List<Task> getAllSite() {
        exit();
        connection = DBConnection.getConnection();
        String sql = "SELECT url, tag  FROM site WHERE enable = 1";
        PreparedStatement statement = null;
        try {
            if (connection == null || connection.isClosed()) {
                connection = DBConnection.getConnection();
            }
            statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            List<Task> list = new ArrayList();
            while (resultSet.next()) {
                Task task = new Task();
                task.setUrl(resultSet.getString(1));
                task.setTag(resultSet.getString(2));
                list.add(task);
            }
            exit();
            return list;
        } catch (SQLException e) {
            logger.error("get all site failed");
            e.printStackTrace();
        }
        return null;
    }

    public boolean addSite(String site) {
        if (site.trim().equalsIgnoreCase("")) {
            logger.error("site string is empty");
            return false;
        }
        String sql = "insert into site (url) values (\"" + site + "\")";
        return execute(sql);
    }

    public boolean enableSite(String site) {
        if (site.trim().equalsIgnoreCase("")) {
            logger.error("site string is empty");
            return false;
        }
        String sql = "update site set enable = 1 where url = \"" + site + "\"";
        return execute(sql);
    }

    public boolean disableSite(String site) {
        if (site.trim().equalsIgnoreCase("")) {
            logger.error("site string is empty");
            return false;
        }
        String sql = "update site set enable = 0 where url = \"" + site + "\"";
        return execute(sql);
    }

}