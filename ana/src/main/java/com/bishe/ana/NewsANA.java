package com.bishe.ana;

import com.bishe.ana.bean.New;
import com.bishe.ana.bean.Topic;
import com.bishe.ana.dao.NewDao;
import com.bishe.ana.dao.TopicDao;
import com.bishe.crawler.dao.NewsDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

public class NewsANA {

    private static final Logger logger = LoggerFactory.getLogger(NewsANA.class);

    //tfidf计算
    private TFIDF tfidf;

    //分词器
    private WordSegmenter wordSegmenter;

    //存储所有的topic
    private List<Topic> topics;

    //Topic数据库操作类
    private TopicDao topicDao;

    private NewDao newDAO;

    //阈值
    private static double THRESHOLD = 0.1;


    static {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader("config/properties.ini"));
            THRESHOLD = Double.parseDouble(properties.getProperty("threshold"));
            logger.info("threshold is [" + THRESHOLD + "]");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private int count = 0;

    public NewsANA() {
        tfidf = new TFIDF();
        this.wordSegmenter = new WordSegmenter();
        this.topicDao = new TopicDao();
        this.newDAO = new NewDao();
        this.getAllTopics();
    }


    //获取所有的topic
    public void getAllTopics() {
        this.topics = topicDao.getAllTopics();
        logger.info("get [" + topics.size() + "] topic form mysql");
    }

    //分析新闻
    public boolean analyseNews(New n) {
        count++;
        if (count == 100000) {
            topics = topics.stream().filter(topic -> isValid(topic)).collect(Collectors.toList());
            count = 0;
        }
        //标题分词
        n.setTitleWords(wordSegmenter.segText(n.getTitle()));
        //内容分词
        Map<String, Double> words = wordSegmenter.segText(n.getContext());
        mergeWords(words, n.getTitleWords());
        n.setWords(words);
        //内容分词结果计算tfidf
        Map<String, Double> metrics = tfidf.getTFIDMetric(n.getWords());
        if (metrics.size() > 100) {
            metrics = metrics.entrySet().stream().sorted(Map.Entry.<String, Double>comparingByValue().reversed()).collect(Collectors.toList()).subList(0, 100).stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
        n.setMetrics(metrics);
        //标题分词结果计算tfidf
        n.setTitleMetrics(tfidf.getTFIDMetric(n.getTitleWords()));
        //取tfidf最高的前五个作为关键词
        if (n.getWords().size() > 5) {
            n.setKeywords(n.getMetrics().entrySet().stream().sorted(Map.Entry.<String, Double>comparingByValue().reversed()).collect(Collectors.toList()).subList(0, 5).stream().map(entry -> entry.getKey()).collect(Collectors.toList()));
        } else {
            n.setKeywords(n.getWords().keySet().stream().collect(Collectors.toList()));
        }
        //根据关键词提取摘要
        n.setAbst(getAbstract(n.getContext(), n.getKeywords()));
        if (cluster(n)) {
            return true;
        }
        return false;
    }

    private void mergeWords(Map<String, Double> words, Map<String, Double> titleWords) {
        titleWords.forEach((key, value) -> {
            if (words.containsKey(key)) {
                words.put(key, 0.5 * words.get(key) + 0.5 * value);
            }
        });
    }

    private boolean isValid(Topic topic) {
        int count = newDAO.getNewsCountByTopic(topic.getName());
        if (count < 2) {
            //不是刚刚加入的topic
            if (System.currentTimeMillis() - topic.getLastUpdateTime().getTime() > 1000L * 60 * 60 * 24) {
                logger.info("remove topic [" + topic.getName() + "]");
                topic.setValid(false);
                topicDao.updateTopicStatus(topic);
                logger.info("remove news [" + count + "]");
                newDAO.deleteNewsByTopic(topic.getName());
                return false;
            }
        }
        return true;
    }


    //获取新闻摘要
    private String getAbstract(String context, List<String> words) {
        StringBuilder abst = new StringBuilder();
        String[] sentences = context.split("[,|，| |.|。|!|？|?|\n|\n\r|\r]");
        int count = 0;
        for (String sentence : sentences) {
            if (sentence.startsWith("原标题")) {
                continue;
            }
            for (int i = 0; i < words.size() && count < words.size(); i++) {
                String word = words.get(i);
                if (sentence.contains(word)) {
                    abst.append(sentence).append(",");
                    count++;
                    break;
                }
            }
        }
        if (abst.length() > 0) {
            return abst.deleteCharAt(abst.length() - 1).append("。").toString();
        } else {
            return "";
        }
    }


    //更新话题模型
    private void updateTopic(Topic topic, New n) {
        n.getMetrics().forEach((word, tfidf) -> {
            if (topic.getWords().containsKey(word)) {
                topic.getWords().put(word, (topic.getWords().get(word) + n.getMetrics().get(word)) / 2);
            } else {
                topic.getWords().put(word, n.getMetrics().get(word));
            }
        });
        if (topic.getWords().size() > 100) {
            topic.setWords(topic.getWords().entrySet().stream().sorted(Map.Entry.<String, Double>comparingByValue().reversed()).collect(Collectors.toList()).subList(0, 100).stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        }
        topic.setLastUpdateTime(new Timestamp(System.currentTimeMillis()));
        topic.setValid(true);
        topicDao.updateTopic(topic);
    }


    public boolean cluster(New n) {
        for (Topic topic : topics) {
            if (topic.getClassID() != n.getClassID()) {
                continue;
            }
            double similarityBase = SimilarityCal.calSimilarity(n.getMetrics(), topic.getWords());
            if (n.getTime() != null) {
                double hours = Math.abs(n.getTimes() - topic.getLastUpdateTime().getTime()) / (1000L * 60 * 60);
                similarityBase = similarityBase * (100.0 / (100 + Math.log(hours)));
            }
            if (similarityBase > 0.9) {
                logger.info("already has same new");
                return false;
            }
            if (similarityBase > THRESHOLD) {
                logger.info("the news [" + n.getTitle() + "] belongs to topic [" + topic.getName() + "]");
                n.setTopic(topic.getName());
                updateTopic(topic, n);
                return true;
            }
        }
        createNewTopicFromNew(n);
        return true;
    }

    private void createNewTopicFromNew(New n) {
        Topic topic = new Topic();
        List<String> topWords = n.getMetrics().entrySet().stream().sorted(Map.Entry.<String, Double>comparingByValue().reversed()).collect(Collectors.toList()).stream().map(entry -> entry.getKey()).collect(Collectors.toList());
        if (topWords.size() > 5) {
            topWords = topWords.subList(0, 5);
        }
        String name = topWords.toString();
        name = name.substring(1, name.length() - 1);
        topic.setName(name);
        topic.setWordsByJsonString(n.getWordsJsonString());
        topic.setValid(true);
        topic.setCreateTime(new Timestamp(System.currentTimeMillis()));
        topic.setLastUpdateTime(new Timestamp(System.currentTimeMillis()));
        topicDao.insertTopic(topic);
        logger.info("create new topic [" + topic.getName() + "]");
        n.setTopic(name);
        topics.add(topic);
    }

    public static void main(String[] args) {

        List<String> a = new ArrayList<>();
        a.add("aaa");
        a.add("bbb");
        System.out.println(a.toArray().toString());

//        NewsANA newsANA = new NewsANA();
//        New n = new New();
//        n.setTitle("美检方公布佛州枪击案枪手视频 提前宣告将犯案");
//        n.setContext("原标题：美检方公布佛州枪击案枪手视频 提前宣告将犯案\n" +
//                "　　中新网6月1日电 综合报道，美国检方日前发布了佛罗里达校园枪击案枪手克鲁兹在犯案前录制的视频，在视频中，他声称自己有意成为下一个校园枪手，并计划“杀死至少20人”。\n" +
//                "\n" +
//                "　　报道称，检方发布了在克鲁兹手机上发现的3个视频。2月14日，佛罗里达州帕克兰市道格拉斯中学发生枪击案，导致17人死亡，多人受伤。这是美国现代史上第二严重的公立学校枪击案。克鲁兹在犯案之前已经因纪律问题而被学校开除。当地时间2018年3月14日，美国佛州校园枪击案枪手克鲁兹出庭受审。布劳沃德郡的州检察官办公室发言人说，这三段视频是控方最近在审前程序中，与辩护律师分享的提控证据之一。\n" +
//                "\n" +
//                "　　克鲁兹被捕后认罪，但未说明其杀人动机。这三段视频也只揭露了克鲁兹感觉自己遭孤立，但没有为其动机提供新线索。\n" +
//                "\n" +
//                "　　克鲁兹面对17项谋杀和17项企图谋杀控状，如果被定罪，他可被判死刑。");
//        newsANA.analyseNews(n);
    }


}