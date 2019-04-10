package com.bishe.crawler.server;

import com.bishe.crawler.task.TaskDetect;
import com.bishe.crawler.url.SiteUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateSite {

    private static final Logger logger = LoggerFactory.getLogger(UpdateSite.class);

    public static void main(String[] args) {
        TaskDetect taskDetect = new TaskDetect();
        SiteUpdate siteUpdate = new SiteUpdate();
        while (true) {
            try {
                long count = taskDetect.getCount("task");
                logger.info("detect the count is [" + count + "]");
                siteUpdate.updateSite();
            } catch (Exception e) {
                try {
                    logger.info("reinitialize the site update");
                    siteUpdate = new SiteUpdate();
                    taskDetect = new TaskDetect();
                } catch (Exception ee) {
                    logger.info("update site failed");
                }
            }
            try {
                logger.info("sleep 10 mintues");
                Thread.sleep(1000L * 60 * 10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
