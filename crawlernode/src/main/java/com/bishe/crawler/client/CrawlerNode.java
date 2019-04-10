package com.bishe.crawler.client;

import com.alibaba.fastjson.JSONObject;
import com.bishe.crawler.analyse.HtmlParser;
import com.bishe.crawler.dao.NewsDAO;
import com.bishe.crawler.jms.JMSReceiver;
import com.bishe.crawler.jms.JMSSender;
import com.bishe.crawler.kafka.CustomKafkaConsumer;
import com.bishe.crawler.kafka.CustomKafkaproducer;
import com.bishe.crawler.kafka.NewsProducer;
import com.bishe.crawler.manage.NodeBean;
import com.bishe.crawler.task.Task;
import com.bishe.crawler.task.TaskConvert;
import com.bishe.crawler.task.TaskReceiver;
import com.bishe.crawler.url.URLFilter;
import com.bishe.crawler.util.PCUtil;
import com.bishe.crawler.util.URLUtil;
import com.bishe.crawler.web.Page;
import com.bishe.crawler.web.PageParserTool;
import com.bishe.crawler.web.RequestAndResponseTool;
import org.omg.SendingContext.RunTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CrawlerNode extends Observable implements Runnable, Observer {

    //当前采集节点已经采集到的数量
    public static long crawlTotalNum = 0;
    public static long crawlThisHour = 0;

    //程序开始时间
    public static long programStartTime = System.currentTimeMillis();

    //程序运行小时数
    public static int programRunHours = 1;

    //程序是否异常重启过
    private static boolean isRestart = false;

    private static int nodeID;

    private static final Logger logger = LoggerFactory.getLogger(CrawlerNode.class);

    //采集深度
    private static int DEPTH_THRESHOLD;

    static {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader("config/properties.ini"));
            DEPTH_THRESHOLD = Integer.parseInt(properties.getProperty("depth"));
            logger.info("depth is [" + DEPTH_THRESHOLD + "]");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private JMSSender urlSender;
    private NewsDAO newsDAO;
    private HeartBeat heartBeat;
    private CustomKafkaproducer kafkaProducer;
    private Reporter reporter;


    private URLFilter urlFilter;


    public CrawlerNode() {
        init();
    }

    private void init() {
        this.urlSender = new JMSSender("url", false);
//        this.newsDAO = NewsDAO.getInstance();
//        this.heartBeat = new HeartBeat();
//        this.reporter = new Reporter();
        this.kafkaProducer = new CustomKafkaproducer("web_news");
        if (!isRestart) {
            this.urlFilter = new URLFilter();
//            this.initNodeID();
//            this.startHeartBeatSchedule();
//            this.startReportSchedule();
        }
        isRestart = true;
//        this.addObserver(this);
    }

    private void initNodeID() {
        JMSReceiver jmsReceiver = new JMSReceiver("id_init", true);
        heartBeat.sendHeartBeat();
        nodeID = PCUtil.getLocalIP().charAt(PCUtil.getLocalIP().length() - 1);
//        while (true) {
//            heartBeat.sendHeartBeat();
//            logger.info("wait message to initialize the ID of crawler node");
//            String message = jmsReceiver.receiveMessage();
//            if (message != null) {
//                NodeBean nodeBean = new NodeBean();
//                nodeBean.getBeanFromString(message);
//                if (nodeBean.getIp().equalsIgnoreCase(PCUtil.getLocalIP()) && nodeBean.getHost().equalsIgnoreCase(PCUtil.getLocalHostName())) {
//                    logger.info("receive the initialize message, [" + message + "]");
//                    nodeID = nodeBean.getId();
//                    logger.info("the id of node is [" + nodeID + "]");
//                    break;
//                }
//            }
//            try {
//                Thread.sleep(1000L * 3);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }

    private void startReportSchedule() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            Logger logger = LoggerFactory.getLogger(this.getClass());

            @Override
            public void run() {
                reporter.sendReport();
                logger.info("send report to server");
            }
        }, 1000, 1000L * 60 * 10);
    }

    private void startHeartBeatSchedule() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            Logger logger = LoggerFactory.getLogger(this.getClass());

            @Override
            public void run() {
                heartBeat.sendHeartBeat();
                logger.info("send heart beat to server");
            }
        }, 1000, 1000L * 60 * 1);
    }


    @Override
    public void run() {
        TaskReceiver taskReceiver = new TaskReceiver("task", false);
        while (true) {
            try {
                Task task = taskReceiver.receiveTask();
                if (task == null) {
                    logger.info("didn't get a task, sleep 3 seconds");
                    Thread.sleep(1000L * 3);
                    continue;
                }
                doCrawler(task);
            } catch (Exception e) {
                logger.info("crawler thread catch a exception");
                e.printStackTrace();
//                super.setChanged();
//                notifyObservers();
            }
        }
    }

    private void doCrawler(Task task) {
        crawlThisHour++;
        crawlTotalNum++;
        logger.info("crawl url [" + task.getUrl() + "] begin, depth : " + task.getDepth());
        Page page = RequestAndResponseTool.sendRequstAndGetResponse(task.getUrl());
        if (page == null) {
            logger.info("get page failed, page is null");
            return;
        }
        logger.info("get the page success");
        page.setTag(task.getTag());
        if (task.getWeight() != 5 && HtmlParser.isWebNewPage(page.getHtml())) {
//            logger.info("judge as web news page, save page to mongoDB");
//            newsDAO.savePage(page, nodeID);
            logger.info("judge as web news page, save page to kakfa");
            kafkaProducer.sendMessage(page.getJsonString(nodeID));
            logger.info("crawler total num :[" + crawlTotalNum + "]");
        }
        if (task.getDepth() <= DEPTH_THRESHOLD) {
            Set<String> links = PageParserTool.getLinks(page, "a");
            logger.info("the page has [" + links.size() + "] links");
            for (String link : links) {
                if (!URLUtil.isLegalURL(link)) {
                    continue;
                }
                if (link.contains(task.getTag())) {
                    if (urlFilter.isContainUrl(link, task.getTag())) {
                        logger.info("the link is repeat");
                        continue;
                    }
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("link", link);
                    jsonObject.put("tag", task.getTag());
                    jsonObject.put("depth", String.valueOf(task.getDepth()));
                    urlSender.sendMessage(jsonObject.toJSONString());
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                System.out.println(link);
                }
                continue;
            }
        }
        logger.info("crawl url [" + task.getUrl() + "] end, depth : " + task.getDepth());
        try {
            logger.info("sleep 10 milliseconds");
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        new Thread(new CrawlerNode()).start();
    }

    @Override
    public void update(Observable o, Object arg) {
        new Thread(new CrawlerNode()).start();
    }
}
