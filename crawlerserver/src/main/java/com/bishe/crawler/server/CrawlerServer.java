package com.bishe.crawler.server;

import com.bishe.crawler.url.URLReceiver;
import com.bishe.crawler.manage.NodeManage;
import com.bishe.crawler.task.TaskDetect;
import com.bishe.crawler.url.SiteUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class CrawlerServer {

    private static Logger logger = LoggerFactory.getLogger(CrawlerServer.class);

    //定时更新网站首页URL
    private SiteUpdate siteUpdate;
    //定时定量检测任务队列长度
    private TaskDetect taskDetect;
    //对Node解析出来的URL进行去重处理并封装成新的任务
    private URLReceiver urlReceiver;
    //管理采集节点
    private NodeManage nodeManage;

    private ReportManage reportManage;

    public CrawlerServer() {
        init();
    }

    public static void main(String[] args) {
        CrawlerServer crawlerServer = new CrawlerServer();
        crawlerServer.startSiteUpdateSchedule();
        crawlerServer.startTaskDetectSchedule();
    }

    public void startTaskDetectSchedule() {
        logger.info("start task detect");
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            Logger logger = LoggerFactory.getLogger(this.getClass());

            @Override
            public void run() {
                long count = taskDetect.getCount("task");
                logger.info("detect result : task remain number is [" + count + "]");
            }
        }, 1000, 1000L * 60 * 1);
    }

    private void startSiteUpdateSchedule() {
        logger.info("start site update");
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long count = taskDetect.getCount("task");
                if (count <= 100) {
                    try {
                        siteUpdate.updateSite();
                    } catch (Exception e) {
                        try {
                            logger.info("reinitialize the site update");
                            siteUpdate = new SiteUpdate();
                            siteUpdate.updateSite();
                        } catch (Exception ee) {
                            logger.info("update site failed, now save bloom filter");
                            boolean re = urlReceiver.getUrlFilter().saveFilterToFile();
                            if (re) {
                                logger.info("save bloom filter successfully");
                            } else {
                                logger.info("save bloom filter failed");
                            }
                        }
                    }
                }
            }
        }, 1000, 1000L * 60 * 1);
    }


    private void init() {
        this.siteUpdate = new SiteUpdate();
        this.nodeManage = new NodeManage("heart_beat", false);
        this.taskDetect = new TaskDetect();
        this.urlReceiver = new URLReceiver("url", false);
        this.reportManage = new ReportManage("report", false);
    }
}
