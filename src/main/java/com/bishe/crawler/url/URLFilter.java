package com.bishe.crawler.url;

import com.alibaba.fastjson.JSONObject;
import com.bishe.crawler.kafka.CustomKafkaConsumer;
import com.bishe.crawler.util.URLUtil;
import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class URLFilter implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(URLFilter.class);
    private HashMap<String, BloomFilter> filters;

    public URLFilter() {
//        init();
    }

    private int count = 0;

    public boolean saveFilterToFile() {
        System.out.println("now save url filter info to file system");
        File dir = new File("filter");
        if (!dir.exists() || dir.isDirectory()) {
            System.out.println("create filter directory");
            dir.mkdir();
        }
        File file = new File("filter/bloomfilter");
        if (!file.exists() || file.isDirectory()) {
            System.out.println("create filter file");
            try {
                file.createNewFile();
            } catch (IOException e) {
                logger.error("create filter file failed");
                e.printStackTrace();
                return false;
            }
        }
        FileOutputStream fileOutputStream = null;
        boolean saveResult = false;
        try {
            fileOutputStream = new FileOutputStream("filter/bloomfilter");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(filters);
            System.out.println("save the number of filter is " + filters.size());
            saveResult = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.info("save bloom filter finished");
        return saveResult;
    }

    private boolean loadFilterFromFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            logger.info("filter file is not exist");
            return false;
        }
        FileInputStream inputStream = null;
        boolean loadResurt = false;
        try {
            inputStream = new FileInputStream(filePath);
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            filters = (HashMap<String, BloomFilter>) objectInputStream.readObject();
            logger.info("load the number of filter is " + filters.size());
            loadResurt = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return loadResurt;
    }

    private void init() {
        count = 0;
        logger.info("add shutdown hook");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (saveFilterToFile()) {
                    System.out.println("save filter to file failed");
                }
            }
        });
        logger.info("load url data from files to BloomFilter");
        if (!loadFilterFromFile("filter/bloomfilter")) {
            logger.info("load bloom filter failed");
            filters = new HashMap<String, BloomFilter>();
        }
    }

    public void loadUrlFromKafka(int num) {
        CustomKafkaConsumer consumer = new CustomKafkaConsumer("load_url_test1");
        consumer.subscribe(Arrays.asList("web_news"));

        int i = 1;

        try {
            while (true) {
                if (i == num) {
                    break;
                }
                ConsumerRecords<String, String> record = consumer.getConsumer().poll(1000);
                for (ConsumerRecord<String, String> consumerRecord : record) {
                    JSONObject jsonObject = null;
                    if (consumerRecord.value() == null) {
                        continue;
                    }
                    try {
                        jsonObject = JSONObject.parseObject(consumerRecord.value());
                    } catch (Exception e) {
                        logger.info("parse json string failed : " + consumerRecord.value());
                        continue;
                    }
                    logger.info("analyse [" + i + "] : [" + jsonObject.getString("url") + "]");
                    try {
                        String url1 = jsonObject.getString("url");
                        String url = url1.substring(url1.indexOf("//") + +2);
                        List<String> sites = Arrays.asList("sina", "sohu", "qq", "people", "xinhuanet", "ifeng", "163", "cctv", "huanqiu", "stnn", "china", "takungpao", "cankaoxiaoxi", "thepaper");
                        for (String site : sites) {
                            if (url.contains(site)) {
                                isContainUrl(url1, site);
                            }
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        saveFilterToFile();

    }

    public boolean isContainUrl(String url, String tag) {
        logger.info(String.valueOf(count));
        if (count == 100) {
            saveFilterToFile();
            count = 0;
        }
        count++;
        if (!filters.containsKey(tag)) {
            logger.info("create bloomfilter for site [" + tag + "]");
            filters.put(tag, BloomFilter.create(new Funnel<String>() {
                private static final long serialVersionUID = 1L;

                @Override
                public void funnel(String arg0, PrimitiveSink arg1) {
                    arg1.putString(arg0, Charsets.UTF_8);
                }
            }, 1024 * 1024, 0.00001d));
        }
        boolean result = filters.get(tag).mightContain(url);
        if (!result) {
            filters.get(tag).put(url);
        }
        return result;
    }

    public static void main(String[] args) throws InterruptedException {
        int count = Integer.parseInt(args[0]);
        URLFilter urlFilter = new URLFilter();
        urlFilter.loadUrlFromKafka(count);
    }


}
