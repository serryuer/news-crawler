package com.bishe.crawler.url;

import com.bishe.crawler.dao.SiteDAO;
import com.bishe.crawler.jms.JMSSender;
import com.bishe.crawler.task.Task;
import com.bishe.crawler.task.TaskConvert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SiteUpdate {

    private static final Logger logger = LoggerFactory.getLogger(SiteUpdate.class);

    private JMSSender taskSender;
    private SiteDAO siteDAO;

    public SiteUpdate(JMSSender taskSender) {
        this.taskSender = taskSender;
        this.siteDAO = new SiteDAO();
    }

    public SiteUpdate() {
        this(new JMSSender("task", false));
    }

    public void updateSite() {
        List<Task> sites = siteDAO.getAllSite();
        logger.info("begin to update site");
        sites.forEach(site -> {
            logger.info("update site : " + site.getUrl());
            site.setStatus(Task.TaskStatus.Created);
            site.setWeight(5);
            site.setDepth(1);
            taskSender.sendMessage(TaskConvert.convertFromBeanToString(site));
        });
    }

    public void exit() {
        taskSender.exit();
        siteDAO.exit();
    }

}
