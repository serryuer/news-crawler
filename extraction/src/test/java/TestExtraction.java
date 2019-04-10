import com.bishe.crawler.web.Page;
import com.bishe.crawler.web.RequestAndResponseTool;
import com.bishe.extraction.bean.WebNew;
import com.bishe.extraction.extraction.DataExtraction;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TestExtraction {

    public static List<String> getUrlsFromFile(String filePath) {
        List<String> urls = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(filePath)));
            String url = "";
            while ((url = reader.readLine()) != null) {
                if (url.trim().equalsIgnoreCase("")) {
                    continue;
                }
                urls.add(url);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return urls;
    }

    public static void writeToFile(String content, String filePath) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(new File(filePath)));
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        List<String> urls = getUrlsFromFile("test_news/urls.txt");
        int i = 0;
        DataExtraction dataExtraction = new DataExtraction();
        for (String url : urls) {
            Page page = RequestAndResponseTool.sendRequstAndGetResponse(url);
            if (page == null)
                continue;
            writeToFile(page.getHtml(), "test_news/src/" + String.valueOf(i));
            WebNew webNew = dataExtraction.extractStructureNewsInfo(page.getHtml(), url, "");
            writeToFile(webNew.getContent(), "test_news/result/" + String.valueOf(i));
            i++;
        }

    }

}
