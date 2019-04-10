package com.bishe.ana.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    private static final Logger logger = LoggerFactory.getLogger(DBConnection.class);
    private static Properties properties;
    private static String ip;
    private static String port;
    private static String username;
    private static String password;
    private static String database;
    private static String encode;

    static {
        properties = new Properties();
        try {
            properties.load(new FileReader("config/properties.ini"));
            ip = properties.getProperty("db.ip");
            port = properties.getProperty("db.port");
            username = properties.getProperty("db.username");
            password = properties.getProperty("db.password");
            database = properties.getProperty("db.database");
            encode = properties.getProperty("db.encode");
        } catch (IOException e) {
            logger.error("load properties file error");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        Connection connection = null;
        try {
            String connectString = "jdbc:mysql://" + ip + ":" + port + "/" + database
                    + "?rewriteBatchedStatements=true&autoReconnect=true&useSSL=true&continueBatchOnError=true&useUnicode=true&characterEncoding=" + encode;
            connection = DriverManager.getConnection(connectString, username, password);
        } catch (SQLException e) {
            logger.error("init Database connection error");
            e.printStackTrace();
        }
        return connection;
    }

    public static void main(String[] args) {
        DBConnection.getConnection();
    }
}
