package com.bishe.ana;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;
import org.apache.commons.io.FileUtils;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;


public class WordSegmenter {


    private boolean isStopWords(String word) {
        Set<String> set = new HashSet<String>();
        try {
            for (Object e : FileUtils.readLines(new File("config/stopwords.txt"))) {
                set.add((String) e);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (set.contains(word)) {
            return true;
        }
        return false;
    }

    /**
     * 获取文本的所有分词结果
     *
     * @param text 文本
     * @return 所有的分词结果，KEY 为分词器模式，VALUE 为分词器结果
     */
//    private Map<String, Double> segText(String text, SegMode segMode) {
//        Map<String, Double> words = new HashMap<>();
//        for (SegToken token : JIEBA_SEGMENTER.process(text, segMode)) {
//            if (isStopWords(token.word.getToken())) {
//                words.put(token.word.getToken(), token.word.getFreq());
//            }
//        }
//        return words;
//    }

    public Map<String, Double> segText(String text) {
        Map<String, Double> words = new HashMap<>();
        IKSegmenter ik = new IKSegmenter(new StringReader(text), true);
        try {
            Lexeme word = null;
            while ((word = ik.next()) != null) {
                if (!isStopWords(word.getLexemeText())) {
                    if (words.containsKey(word.getLexemeText())) {
                        words.put(word.getLexemeText(), words.get(word.getLexemeText()) + 1);
                    } else {
                        words.put(word.getLexemeText(), 1.0);
                    }
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        final double[] sum = {0};
        words.forEach((k, v) -> {
            sum[0] += v;
        });
        words.forEach((k, v) -> words.put(k, v / sum[0]));
        return words;
    }


    public static void main(String[] args) {
        WordSegmenter wordSegmenter = new WordSegmenter();
        System.out.println(wordSegmenter.segText("事先人工建立好分词词典和分词规则库立好分词词典。").toString());
    }

}
