package com.bishe.extraction.util;

import com.bishe.crawler.web.Page;
import org.apache.commons.httpclient.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateConvert {

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);


    private static Date formatDate(String date) {
        try {
            if (date.length() == "yyyy-MM-dd HH:mm:ss".length()) {
                return format.parse(date);
            } else if (date.length() == "yyyy-MM-dd HH:mm".length()) {
                return format1.parse(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String convertDateTostring(String rawStr) {
        int year = 0, month = 0, day = 0, hour = 0, min = 0, sec = 0;
        String timeStr = rawStr.trim().replaceAll(" ", "");
        String regex1 = "201[0-8]年[0-9]{2}月[0-9]{2}日 [0-9]{2}:[0-9]{2}:[0-9]{2}";
        String regex2 = "201[0-8]年[0-9]{2}月[0-9]{2}日 [0-9]{2}:[0-9]{2}";
        String regex3 = "201[0-8]年[0-9]{2}月[0-9]{2}日 [0-9]{2}";
        String regex4 = "201[0-8]年[0-9]{2}月[0-9]{2}日";
        String regex5 = "201[0-8]\\-[0-9]{2}\\-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}";
        String regex6 = "201[0-8]\\-[0-9]{2}\\-[0-9]{2} [0-9]{2}:[0-9]{2}";
        String regex7 = "201[0-8]\\-[0-9]{2}\\-[0-9]{2} [0-9]{2}";
        String regex8 = "201[0-8]\\-[0-9]{2}\\-[0-9]{2}";
        Matcher matcher = Pattern.compile(regex1).matcher(rawStr);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date = new Date();
        if (matcher.find()) {
            System.out.println(matcher.group());
            try {
                date = simpleDateFormat.parse(matcher.group());
                return format.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                logger.info("convert time str failed [" + rawStr + "], [" + matcher.group() + "]");
                return " ";
            }
        } else {
            simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
            matcher = Pattern.compile(regex2).matcher(rawStr);
            if (matcher.find()) {
                System.out.println(matcher.group());
                try {
                    date = simpleDateFormat.parse(matcher.group());
                    return format.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                    logger.info("convert time str failed [" + rawStr + "], [" + matcher.group() + "]");
                    return " ";
                }
            } else {
                simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH");
                matcher = Pattern.compile(regex3).matcher(rawStr);
                if (matcher.find()) {
                    System.out.println(matcher.group());
                    try {
                        date = simpleDateFormat.parse(matcher.group());
                        return format.format(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        logger.info("convert time str failed [" + rawStr + "], [" + matcher.group() + "]");
                        return " ";
                    }
                } else {
                    simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
                    matcher = Pattern.compile(regex4).matcher(rawStr);
                    if (matcher.find()) {
                        System.out.println(matcher.group());
                        try {
                            date = simpleDateFormat.parse(matcher.group());
                            return format.format(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            logger.info("convert time str failed [" + rawStr + "], [" + matcher.group() + "]");
                            return " ";
                        }
                    } else {
                        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        matcher = Pattern.compile(regex5).matcher(rawStr);
                        if (matcher.find()) {
                            System.out.println(matcher.group());
                            try {
                                date = simpleDateFormat.parse(matcher.group());
                                return format.format(date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                                logger.info("convert time str failed [" + rawStr + "], [" + matcher.group() + "]");
                                return " ";
                            }
                        } else {
                            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            matcher = Pattern.compile(regex6).matcher(rawStr);
                            if (matcher.find()) {
                                System.out.println(matcher.group());
                                try {
                                    date = simpleDateFormat.parse(matcher.group());
                                    return format.format(date);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    logger.info("convert time str failed [" + rawStr + "], [" + matcher.group() + "]");
                                    return " ";
                                }
                            } else {
                                simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH");
                                matcher = Pattern.compile(regex7).matcher(rawStr);
                                if (matcher.find()) {
                                    System.out.println(matcher.group());
                                    try {
                                        date = simpleDateFormat.parse(matcher.group());
                                        return format.format(date);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        logger.info("convert time str failed [" + rawStr + "], [" + matcher.group() + "]");
                                        return " ";
                                    }
                                } else {
                                    simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    matcher = Pattern.compile(regex8).matcher(rawStr);
                                    if (matcher.find()) {
                                        System.out.println(matcher.group());
                                        try {
                                            date = simpleDateFormat.parse(matcher.group());
                                            return format.format(date);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                            logger.info("convert time str failed [" + rawStr + "], [" + matcher.group() + "]");
                                            return " ";
                                        }
                                    } else {
                                        System.out.println("failed");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return " ";
    }


    public static long getDateMillis(String rawDate) {
        String date = convertDateTostring(rawDate);
        try {
            Date date1 = formatDate(date);
            if (date1 == null) {
                logger.error("convert time string failed [" + rawDate + "]");
                return 0;
            }
            return date1.getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void main(String[] args) {
        String time = "zheshi1beeka hkasd2018-05-15 17:46:18　来源: 轻松一刻工作室";


        System.out.println(DateConvert.getDateMillis(time));
    }

}
