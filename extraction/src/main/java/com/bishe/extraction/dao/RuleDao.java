package com.bishe.extraction.dao;

import com.bishe.crawler.dao.BaseDAO;
import com.bishe.crawler.dao.DBConnection;
import com.bishe.crawler.dao.SiteDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RuleDao extends BaseDAO {
    private static final Logger logger = LoggerFactory.getLogger(SiteDAO.class);

    private Connection connection;

    public RuleDao() {
        super();
    }

    public List<String> getRulesByDomain(String domain) {
        String sql = "SELECT rule  FROM extract_rule WHERE domain = \"" + domain + "\" and is_valid = 1";
        PreparedStatement statement = null;
        try {
            if (connection == null || connection.isClosed()) {
                connection = DBConnection.getConnection();
            }
            statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            List<String> list = new ArrayList();
            while (resultSet.next()) {
                list.add(resultSet.getString(1));
            }
            return list;
        } catch (SQLException e) {
            logger.error("get all site failed");
            e.printStackTrace();
        }
        return null;
    }

    public boolean insertOneRule(String domain, String rule, String tag) {
        String sql = "INSERT INTO extract_rule (domain, rule, tag) VALUES(?, ? ,?)";
        PreparedStatement statement = null;
        try {
            if (connection == null || connection.isClosed()) {
                connection = DBConnection.getConnection();
            }
            statement = connection.prepareStatement(sql);
            statement.setString(1, domain);
            statement.setString(2, rule);
            statement.setString(3, "sina");
            boolean re = statement.execute();
            return re;
        } catch (SQLException e) {
            logger.error("execute sql error");
            e.printStackTrace();
        }
        return false;

    }
}
