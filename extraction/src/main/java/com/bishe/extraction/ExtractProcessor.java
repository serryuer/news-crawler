package com.bishe.extraction;

import com.alibaba.fastjson.JSONObject;
import com.bishe.extraction.bean.WebNew;
import com.bishe.extraction.extraction.DataExtraction;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

import javax.xml.crypto.Data;
import java.sql.Timestamp;

public class ExtractProcessor implements Processor {

    private DataExtraction extraction;

    @Override
    public void init(ProcessorContext context) {
        extraction = new DataExtraction();
    }

    @Override
    public void process(Object dummy, Object line) {
        String value = (String) line;
        JSONObject jsonObject = JSONObject.parseObject(value);
        WebNew webNew = extraction.extractStructureNewsInfo(jsonObject.getString("content"), jsonObject.getString("url"), jsonObject.getString("tag"));
    }

    @Override
    public void punctuate(long timestamp) {

    }

    @Override
    public void close() {
    }
}
