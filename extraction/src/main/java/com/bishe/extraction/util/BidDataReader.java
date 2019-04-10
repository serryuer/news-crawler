package com.bishe.extraction.util;

import java.io.*;

public class BidDataReader {
    /*
     * author:合肥工业大学 管院学院 钱洋
     * email：1563178220@qq.com
     */
    public static void main(String[] args) throws IOException {
        long timer = System.currentTimeMillis();
        int bufferSize = 20 * 1024 * 1024;//设读取文件的缓存为20MB
        //建立缓冲文本输入流
        File file = new File("C:\\Users\\serryu\\Downloads\\news_tensite_xml.full\\news_tensite_xml.dat");
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        //注意这里有时会乱码，根据自己的文本存储格式，进行调整
        InputStreamReader inputStreamReader = new InputStreamReader(bufferedInputStream, "gbk");
        BufferedReader input = new BufferedReader(inputStreamReader, bufferSize);
        //要分割的块数减一,这里表示分割为31个文件
        int splitNum = 30;
        //12046表示我的输入本文的行数，我的文本12046行，由于每行文本较长，所有存储占用较大
        int fileLines = 7763543;
        //分割后存储每个块的行数
//        long perSplitLines = fileLines / splitNum;
//        for (int i = 0; i <= splitNum; ++i){
        //分割
        //每个块建立一个输出
        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("C:\\Users\\serryu\\Downloads\\news_tensite_xml.full\\parts\\a.txt")), "gbk"));
        String line = null;
        //逐行读取，逐行输出
        for (long lineCounter = 0; lineCounter < fileLines && (line = input.readLine()) != null; ++lineCounter) {
            if (line.startsWith("<content>") && line.length() > 100) {

                output.append(line.substring(9, line.length() - 10) + "\r");
            }
        }
        output.flush();
        output.close();
        output = null;
//        }
        input.close();
        timer = System.currentTimeMillis() - timer;
        //我的1.6g数据不要1分钟，分割完毕
        System.out.println("处理时间：" + timer);
    }

}