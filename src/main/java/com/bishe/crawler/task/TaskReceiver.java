package com.bishe.crawler.task;

import com.bishe.crawler.jms.JMSReceiver;

import javax.jms.Connection;

public class TaskReceiver extends JMSReceiver {

    public TaskReceiver(Connection con, String queue, boolean isTopic) {
        super(con, queue, isTopic);
    }

    public TaskReceiver(String queue, boolean isTopic) {
        super(queue, isTopic);
    }

    public Task receiveTask() {
        String taskString = this.receiveMessage();
        if (taskString == null) {
            return null;
        }
        Task task = TaskConvert.convertFromStringToBean(taskString);
        return task;
    }
}
