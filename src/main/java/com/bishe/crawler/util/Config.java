package com.bishe.crawler.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Config {

    private static Logger logger = LoggerFactory.getLogger(Config.class);

    public static Properties properties;

    static {
        logger.info("load config files to program");
        properties = new Properties();
        try {
            properties.load(new FileReader("config/properties.ini"));
        } catch (IOException e) {
            logger.error("load config files failed");
            System.exit(-1);
            e.printStackTrace();
        }
    }

    public static String getProperty(String name) {
        if (properties.containsKey(name)) {
            return properties.getProperty(name);
        } else {
            logger.error("no value found for key : " + name);
            return null;
        }
    }
}
