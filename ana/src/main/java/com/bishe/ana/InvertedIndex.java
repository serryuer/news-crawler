package com.bishe.ana;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class InvertedIndex implements Serializable {

    public long getTotalDataNum() {
        return totalDataNum;
    }

    public void setTotalDataNum(long totalDataNum) {
        this.totalDataNum = totalDataNum;
    }

    //文档总数
    private long totalDataNum;

    //分词器
    private static WordSegmenter wordSegmenter = new WordSegmenter();

    private AtomicLong count = new AtomicLong(0);

    //原始数据
    private Map<Long, String> datas = new HashMap<Long, String>();
    //索引文件
    private TreeMap<String, Long> indexs = new TreeMap<>();

    public InvertedIndex() {
        totalDataNum = 0;
    }

    /**
     * 添加被索引的数据
     *
     * @param str
     */
    public void add(String str) {
        //自动生成编号，因为编号是自动生成的，所以不支持修改已索引的数据
        totalDataNum++;
        //分词后处理每个word
        for (String tmp : wordSegmenter.segText(str).keySet()) {
            String key = tmp;
            if (indexs.containsKey(key)) {
                indexs.put(key, indexs.get(key) + 1);
            } else {
                indexs.put(key, 1L);
            }
        }
    }

    public void addWords(Set<String> words) {
        totalDataNum++;
        words.forEach((word) -> {
            if (indexs.containsKey(word)) {
                indexs.put(word, indexs.get(word) + 1);
            } else {
                indexs.put(word, 1L);
            }
        });
    }

    public long search(String key) {
        return indexs.get(key);
    }
}
