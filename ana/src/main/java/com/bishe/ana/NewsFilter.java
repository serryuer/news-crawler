package com.bishe.ana;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class NewsFilter implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(com.bishe.crawler.url.URLFilter.class);
    private BloomFilter filter;

    public NewsFilter() {
        init();
    }

    private static int count = 0;

    public boolean saveFilterToFile() {
        System.out.println("now save news filter info to file system");
        File dir = new File("filter");
        if (!dir.exists() || dir.isDirectory()) {
            System.out.println("create filter directory");
            dir.mkdir();
        }
        File file = new File("filter/newsfilter");
        if (!file.exists() || file.isDirectory()) {
            System.out.println("create filter file");
            try {
                file.createNewFile();
            } catch (IOException e) {
                logger.error("create newsfilter file failed");
                e.printStackTrace();
                return false;
            }
        }
        FileOutputStream fileOutputStream = null;
        boolean saveResult = false;
        try {
            fileOutputStream = new FileOutputStream("filter/newsfilter");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(filter);
            saveResult = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.info("save bloom filter finished");
        return saveResult;
    }

    private boolean loadFilterFromFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            logger.info("newsfilter file is not exist");
            return false;
        }
        FileInputStream inputStream = null;
        boolean loadResurt = false;
        try {
            inputStream = new FileInputStream(filePath);
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            filter = (BloomFilter) objectInputStream.readObject();
            loadResurt = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return loadResurt;
    }

    private void init() {
        logger.info("add shutdown hook");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (saveFilterToFile()) {
                    System.out.println("save filter to file failed");
                }
            }
        });
        logger.info("load url data from files to BloomFilter");
        if (!loadFilterFromFile("filter/newsfilter")) {
            logger.info("load bloom filter failed, now initialize the filter");
            filter = BloomFilter.create(new Funnel<String>() {
                private static final long serialVersionUID = 1L;

                @Override
                public void funnel(String arg0, PrimitiveSink arg1) {
                    arg1.putString(arg0, Charsets.UTF_8);
                }
            }, 1024 * 1024 * 10, 0.00001d);
        }
    }

    public boolean isContainTitle(String url) {
        if (count == 100) {
            saveFilterToFile();
            count = 0;
        }
        count++;
        boolean result = filter.mightContain(url);
        if (!result) {
            filter.put(url);
        }
        return result;
    }

    public static void main(String[] args) throws InterruptedException {
        com.bishe.crawler.url.URLFilter urlFilter = new com.bishe.crawler.url.URLFilter();
        Thread.sleep(1000 * 3);
        System.exit(0);
    }


}

