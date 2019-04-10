package com.bishe.extraction.kafka;

import com.bishe.crawler.kafka.KafkaComponent;
import com.bishe.crawler.web.RequestAndResponseTool;
import com.bishe.extraction.bean.WebNew;
import com.bishe.extraction.extraction.DataExtraction;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImportFromMongoDB extends KafkaComponent {

    private static final Logger logger = LoggerFactory.getLogger(ImportFromMongoDB.class);


    private static final String STRUCTURE_NEWS_TOPIC = "raw_news";

    private Producer producer;

    public ImportFromMongoDB() {
        init();
    }

    @Override
    public void init() {
        super.init();
        this.producer = new KafkaProducer(props);
    }

    public void sendMessage(String message) {
        producer.send(new ProducerRecord(STRUCTURE_NEWS_TOPIC, message));
    }


    public static void main(String[] args) {
        DataExtraction dataExtraction = new DataExtraction();
        ImportFromMongoDB importFromMongoDB = new ImportFromMongoDB();
        WebNew webNew = dataExtraction.extractStructureNewsInfo(
                RequestAndResponseTool.sendRequstAndGetResponse("http://news.sina.com.cn/c/xl/2018-05-16/doc-iharvfht9465254.shtml").getHtml(),
                "http://news.sina.com.cn/c/xl/2018-05-16/doc-iharvfht9465254.shtml",
                "sina");
        importFromMongoDB.sendMessage(webNew.toJsonString());

    }

}
