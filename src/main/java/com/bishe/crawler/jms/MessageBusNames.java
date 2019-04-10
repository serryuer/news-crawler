package com.bishe.crawler.jms;

public interface MessageBusNames {
	public static final String Task="task";
	public static final String ControlS2C="CONTROLS2C";
	public static final String CrawlInfoC2S="CRAWLINFOC2S";
	public static final String KeyID="KEYID";
	public static final String NormalID="NORMALID";
	public static final String ReportTwitterWEB="REPORTTWITTERWEB";
	public static final String ReportTwitterAPI="REPORTTWITTERAPI";
	
	public static final String UrgentTask="UrgentTask";
	public static final String KeyWordAndTopicTask="KeyWordAndTopicTask";
	public static final String KeyUserTask="KeyUserTask";
	public static final String PollTask = "PollTask";	

	
	
	public String[] names={Task,ControlS2C,CrawlInfoC2S,KeyID,NormalID,ReportTwitterWEB,ReportTwitterAPI,UrgentTask,KeyWordAndTopicTask,KeyUserTask,Task};
	

}
