package com.bishe.ana;

import com.bishe.ana.bean.New;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class TFIDF {

    private static final Logger logger = LoggerFactory.getLogger(TFIDF.class);

    private InvertedIndex invertedIndex;

    private int count;

    public TFIDF() {
        count = 0;
        init();
    }

    private void init() {
        logger.info("init inverted index");
        File indexFile = new File("index.dat");
        if (indexFile.exists()) {
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(indexFile)));
                invertedIndex = (InvertedIndex) objectInputStream.readObject();
                objectInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return;
        }
        invertedIndex = new InvertedIndex();
        File file = new File("config/data/data.txt");
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            //注意这里有时会乱码，根据自己的文本存储格式，进行调整
            InputStreamReader inputStreamReader = new InputStreamReader(bufferedInputStream, "gbk");
            BufferedReader input = new BufferedReader(inputStreamReader, 20 * 1024 * 1024);
            int fileLines = 2000;
            String line = "";
            for (long lineCounter = 0; lineCounter < fileLines && (line = input.readLine()) != null; ++lineCounter) {
                invertedIndex.add(line);
                System.out.println(lineCounter);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        saveMaptoFile();
    }

    public void saveMaptoFile() {
        logger.info("save map info to file");
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(new File("index.dat"))));
            objectOutputStream.writeObject(invertedIndex);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Double> getTFIDMetric(Map<String, Double> words) {
        count++;
        if (count == 1000) {
            saveMaptoFile();
            count = 0;
        }
        invertedIndex.addWords(words.keySet());
        Map<String, Double> metrics = new HashMap<>();
        words.forEach((word, termFreq) -> {
            metrics.put(word, termFreq / ((double) invertedIndex.search(word) / invertedIndex.getTotalDataNum()));
        });
        return metrics;
    }

    private Map<String, Double> calTFIDF(New n) {
        return null;


    }

    public static void main(String[] args) {
        TFIDF tfidf = new TFIDF();
    }


}
