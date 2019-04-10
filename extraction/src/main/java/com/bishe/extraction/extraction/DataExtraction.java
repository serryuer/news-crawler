package com.bishe.extraction.extraction;

import com.bishe.crawler.util.URLUtil;
import com.bishe.crawler.web.RequestAndResponseTool;
import com.bishe.extraction.bean.ExtractRule;
import com.bishe.extraction.bean.WebNew;
import com.bishe.extraction.dao.RuleDao;
import com.bishe.extraction.util.DateConvert;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DataExtraction {

    private static final Logger logger = LoggerFactory.getLogger(DataExtraction.class);

    private RuleDao ruleDao;

    private Document document;

    public DataExtraction() {
        ruleDao = new RuleDao();
    }

    /**
     * 抽取网页结构化信息
     *
     * @param html 网页源码数据
     * @param url  网页URL
     * @param tag  网页标签
     * @return 新闻的结构化信息
     */
    public WebNew extractStructureNewsInfo(String html, String url, String tag) {
        document = Jsoup.parse(html);
        String domain = URLUtil.getHost(url);
        if (domain == null) {
            return null;
        }
//        List<String> rules = ruleDao.getRulesByDomain(domain);
        boolean extractResult = false;
        WebNew webNew = null;
//        if (rules != null && rules.size() > 0) {
//        if (false) {
//            logger.info("extract info by existing rule");
//            logger.info("the url is : [" + url + "]");
//            webNew = extractByRule(rules);
//            if (webNew != null) {
//                return webNew;
//            }
//        }

        webNew = extract(html, domain, tag);
        return webNew;
    }


    /**
     * 根据已有规则抽取数据
     *
     * @param rules 已有规则
     * @return
     */
    private WebNew extractByRule(List<String> rules) {
        for (String rule : rules) {
            logger.info("the rule is [" + rule + "]");
            ExtractRule rule1 = ExtractRule.fromJsonString(rule);
            Element contentElement = null;
            if ((contentElement = document.getElementById(rule1.getContentid())) != null || (contentElement = document.getElementsByClass(rule1.getContentid()).first()) != null) {
                WebNew webNew = new WebNew();
                webNew.setTitle(document.title());
                webNew.setTitle(document.head().getElementsByTag("title").text());
//                if (!rule1.getTitleid().trim().equals("")) {
//                    if (document.getElementById(rule1.getTitleid()) != null) {
//                        webNew.setTitle(document.getElementById(rule1.getTitleid()).text());
//                    } else if (document.getElementsByClass(rule1.getTitleid()) != null) {
//                        webNew.setTitle(document.getElementsByClass(rule1.getTitleid()).first().text());
//                    } else {
//                        webNew.setTitle("*");
//                    }
//                }
                if (!rule1.getTimeid().trim().equals("")) {
                    if (document.getElementById(rule1.getTimeid()) != null) {
                        if (document.getElementById(rule1.getTimeid()) != null) {
                            webNew.setTimeStr(DateConvert.convertDateTostring(document.getElementById(rule1.getTimeid()).text()));
                            webNew.setTime(new Timestamp(DateConvert.getDateMillis(document.getElementById(rule1.getTimeid()).text())));
                        } else {
                            webNew.setTimeStr("*");
                        }
                    } else if (document.getElementsByClass(rule1.getTimeid()) != null) {
                        if (document.getElementsByClass(rule1.getTimeid()).size() != 0) {
                            webNew.setTimeStr(DateConvert.convertDateTostring(document.getElementsByClass(rule1.getTimeid()).first().text()));
                            webNew.setTime(new Timestamp(DateConvert.getDateMillis(document.getElementsByClass(rule1.getTimeid()).text())));
                        } else {
                            webNew.setTimeStr("*");
                        }
                    } else {
                        webNew.setTimeStr("*");
                    }
                } else {
                    webNew.setTimeStr("");
                }
                if (!rule1.getAuthorid().trim().equals("")) {
                    if (document.getElementById(rule1.getAuthorid()) != null) {
                        if (document.getElementById(rule1.getAuthorid()) != null) {
                            webNew.setAuthor(getAuthorFromString(document.getElementById(rule1.getAuthorid()).text()));
                        } else {
                            webNew.setAuthor("*");
                        }
                    } else if (document.getElementsByClass(rule1.getAuthorid()) != null) {
                        if (document.getElementsByClass(rule1.getAuthorid()).size() != 0) {
                            webNew.setAuthor(getAuthorFromString(document.getElementsByClass(rule1.getAuthorid()).first().text()));
                        } else {
                            webNew.setAuthor("*");
                        }
                    } else {
                        webNew.setAuthor("*");
                    }
                } else {
                    webNew.setAuthor("");
                }
                preprocess(document);
                contentElement = document.getElementById(rule1.getContentid());
                if (contentElement == null) {
                    contentElement = document.getElementsByClass(rule1.getContentid()).first();
                }
                if (contentElement == null) {
                    return null;
                }
                webNew.setContent(contentElement.text());
                String titleStr = webNew.getContent().substring(0, 100);
//                if (titleStr.contains("原标题:"))

                return webNew;
            }
        }
        return null;
    }


    /**
     * 没有可用抽取规则，直接利用启发式算法抽取数据
     *
     * @param htmlContent
     * @param domain
     * @param tag
     * @return
     */
    private WebNew extract(String htmlContent, String domain, String tag) {
        WebNew webNew = new WebNew();
        ExtractRule rule = new ExtractRule();
        rule.setTitleid(extractTitleInfo(webNew));
        rule.setAuthorid(extractAuthorInfo(webNew));
        rule.setTimeid(extractTimeInfo(webNew));
        this.preprocess(document);
        Node body = document.body();
        if (body == null) {
            return null;
        }
        MyNode tree = MyNode.create(body);
        tree.markContent();
        double bestScore = Double.MIN_VALUE;
        MyNode bestNode = null;
        Queue<MyNode> nodes = new LinkedList<>();
        nodes.add(tree);
        while (nodes.peek() != null) {
            MyNode node = nodes.poll();
            if (node.children != null) {
                node.children.forEach(n -> nodes.add(n));
            }
            if (node.isTextNode()) {
                continue;
            }
            if (!node.isContent) {
                continue;
            }
            if (!node.node.nodeName().trim().equals("div") && !node.node.nodeName().trim().equals("p")) {
                continue;
            }
            double score = node.densitySum;
            if (score > bestScore) {
                bestNode = node;
                bestScore = score;
            }
        }
        if (bestNode == null) {
            return null;
        }
        String contentid = bestNode.node.attr("id");
        if (contentid.trim().equals("")) {
            contentid = bestNode.node.attr("class");
        }
        String content = ((Element) bestNode.node).text();
        if (content.length() > 250) {
            webNew.setContent(content);
        } else {
            return null;
        }
        rule.setContentid(contentid);
        String titleStr = webNew.getContent().substring(0, 100).trim();
        String title = "";
        if (titleStr.startsWith("原标题:")) {
            if (titleStr.endsWith(" ") || title.endsWith("\n\r") || title.endsWith("\r\n") || title.endsWith("\n")) {
                title = titleStr.trim().substring(titleStr.indexOf(":"));
            }
        } else if (titleStr.startsWith("原标题：")) {
            if (titleStr.endsWith(" ") || title.endsWith("\n\r") || title.endsWith("\r\n") || title.endsWith("\n")) {
                title = titleStr.substring(titleStr.indexOf("："), titleStr.indexOf(" "));
            }
        }
        if (title.length() > 5) {
            webNew.setTitle(title);
        }
//        ruleDao.insertOneRule(domain, rule.toJsonString(), tag);
        return webNew;
    }


    /**
     * 抽取新闻网页的时间信息
     *
     * @param webNew
     * @return
     */
    private String extractTimeInfo(WebNew webNew) {
        Elements elements = document.getElementsMatchingOwnText("^\\s*201[0-8]年[0-9]{2}月[0-9]{2}日*");
        Element element = null;
        if (elements.size() > 0) {
            element = elements.first();
        } else {
            elements = document.getElementsMatchingOwnText("^\\s*201[0-8]\\-[0-9]{2}\\-[0-9]{2}.*");
            if (elements.size() > 0) {
                element = elements.first();
            }
        }
        if (element == null) {
            webNew.setTime(new Timestamp(System.currentTimeMillis()));
            webNew.setTimeStr(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            return "";
        }
        String text = element.text();
        if (text.length() > 50) {
            text = text.substring(0, 50);
        }
        try {
            webNew.setTime(new Timestamp(DateConvert.getDateMillis(text)));
            webNew.setTimeStr(DateConvert.convertDateTostring(text));
        } catch (Exception e) {
            webNew.setTime(new Timestamp(System.currentTimeMillis()));
            webNew.setTimeStr(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            return "";
        }
        return element.id().equals("") ? element.className() : element.id();
    }

    /**
     * 抽取网页的作者信息
     *
     * @param webNew
     * @return
     */
    private String extractAuthorInfo(WebNew webNew) {
        Elements elements = document.getElementsMatchingText(Pattern.compile("^责任编辑"));
        String author = "";
        Element element = null;
        if (elements.size() > 0) {
            for (int i = 0; i < elements.size(); i++) {
                element = elements.get(i);
                if (element.text().length() > 20) {
                    continue;
                }
                String text = element.text();
                author = getAuthorFromString(text);
                if (author.trim().equals("")) {
                    continue;
                }
            }
        } else {
            elements = document.getElementsMatchingText(Pattern.compile("^责编"));
            if (elements.size() > 0) {
                for (int i = 0; i < elements.size(); i++) {
                    element = elements.get(i);
                    if (element.text().length() > 20) {
                        continue;
                    }
                    String text = element.text();
                    author = getAuthorFromString(text);
                    if (author.trim().equals("")) {
                        continue;
                    }
                }
            } else {
                elements = document.getElementsMatchingText(Pattern.compile("^作者"));
                if (elements.size() > 0) {
                    for (int i = 0; i < elements.size(); i++) {
                        element = elements.get(i);
                        if (element.text().length() > 20) {
                            continue;
                        }
                        String text = element.text();
                        author = getAuthorFromString(text);
                        if (author.trim().equals("")) {
                            continue;
                        }
                    }
                }
            }
        }
        if (element != null) {
            webNew.setAuthor(author);
            return element.id().equals("") ? element.className() : element.id();
        } else {
            webNew.setAuthor("");
            return "";
        }
    }

    /**
     * 根据字符串提取作者名字
     *
     * @param text
     * @return
     */
    private String getAuthorFromString(String text) {
        String author = "";
        if (text.contains(":")) {
            author = text.substring(text.lastIndexOf(":"));
        } else if (text.contains("：")) {
            author = text.substring(text.lastIndexOf("：") + 1).trim();
        }
        return author;
    }

    /**
     * 抽取新闻的标题信息
     *
     * @param webNew
     * @return
     */
    private String extractTitleInfo(WebNew webNew) {
        webNew.setTitle(document.title());
        webNew.setTitle(document.head().getElementsByTag("title").text());
        if (webNew.getTitle().contains("_")) {
            webNew.setTitle(webNew.getTitle().substring(0, webNew.getTitle().indexOf('_')));
        }
        return "";
    }

    /**
     * 网页源码的预处理（为了抽取正文）
     *
     * @param document
     */
    private void preprocess(Document document) {
        List<String> mergingTag = Arrays.asList("p", "br", "li", "table", "tbody", "tr", "td", "theader", "tfooter");
        mergingTag.forEach((tag) -> {
            for (Element element : document.getElementsByTag(tag)) {
                element.unwrap();
            }
        });
        List<String> ignoreTag = Arrays.asList("head", "meta", "script", "link", "style", "form", "option", "header", "footer", "nav", "noscript");
        ignoreTag.forEach((tag) -> {
            document.getElementsByTag(tag).forEach((element) -> {
                element.remove();
            });
        });
        document.getElementsByAttributeValueContaining("style", "display:none").forEach((element) -> element.remove());
    }

    static class MyNode {
        private double threshold;

        private Node node;
        private List<MyNode> children;
        private MyNode parent;
        private boolean isContent;

        private int characters;
        private int tags;
        private int linkCharacters;
        private int linkTags;

        private double textDensity;
        private double compositeTextDensity;
        private double densitySum;

        private MyNode(Node node, List<MyNode> children) {
            this.children = children;
            this.node = node;
            this.parent = null;
            this.isContent = false;
            this.extractFeatures();
        }

        /**
         * 计算节点的属性数据（字符数、标签数等）
         */
        private void extractFeatures() {
            if (this.isTextNode()) {
                this.characters = ((TextNode) this.node).text().length();
                this.tags = 0;
                this.linkCharacters = 0;
                this.linkTags = 0;
            } else if (children == null) {
                System.out.println(node.nodeName() + "/" + node.parent().toString() + "/" + node.toString());
            } else {
                this.characters = (int) this.children.stream().mapToInt((n) -> n.characters).summaryStatistics().getSum();
                this.tags = (int) this.children.stream().mapToInt((n) -> n.tags).summaryStatistics().getSum();
                List<String> list = Arrays.asList("div", "span", "p", "br", "li");
                if (!list.contains(node.nodeName())) {
                    tags += 1;
                }
                if (node.nodeName() == "a") {
                    linkCharacters = characters;
                    linkTags = 1;
                } else {
                    linkCharacters = (int) this.children.stream().mapToInt((n) -> n.linkCharacters).summaryStatistics().getSum();
                    linkTags = (int) this.children.stream().mapToInt((n) -> n.linkTags).summaryStatistics().getSum();
                }
            }
            textDensity = 1.0 * Math.max(characters, 1) / Math.max(tags, 1);
        }

        /**
         * 是否为文本节点
         *
         * @return
         */
        private boolean isTextNode() {
            return this.node instanceof TextNode;
        }

        /**
         * 初始化节点树
         *
         * @param node
         * @return
         */
        public static MyNode create(Node node) {
            preprocess(node);
            return MyNode._create(node);
        }


        /**
         * 递归构造节点
         *
         * @param node
         * @return
         */
        private static MyNode _create(Node node) {
            if (node instanceof Element || node instanceof Document) {
                List<Node> children = node.childNodes();
                children = children.stream().filter((n) -> MyNode.isValidNode(n)).collect(Collectors.toList());
                List<MyNode> childrens = children.stream().map((n) -> MyNode._create(n)).collect(Collectors.toList());
                childrens = childrens.stream().filter(n -> n != null).collect(Collectors.toList());
                MyNode myNode = new MyNode(node, childrens);
                childrens.forEach((child) -> child.parent = myNode);
                return myNode;
            } else if (node instanceof TextNode) {
                return new MyNode(node, null);
            } else {
                return null;
            }
        }


        /**
         * 判断节点是否有效
         *
         * @param node
         * @return
         */
        private static boolean isValidNode(Node node) {
            if (node instanceof Document || node instanceof TextNode) {
                return !node.toString().trim().equals("");
            }
            return true;
        }

        /**
         * 节点的预处理
         *
         * @param node
         * @return
         */
        private static Node preprocess(Node node) {
            TextNode firstTextNode = null;
            for (Node n : node.childNodes()) {
                if (n instanceof TextNode) {
                    if (firstTextNode != null) {
                        firstTextNode.text(firstTextNode.text() + ((TextNode) n).text());
                        ((TextNode) n).text("");
                    } else {
                        firstTextNode = (TextNode) n;
                    }
                } else if (n instanceof Element) {
                    firstTextNode = null;
                    preprocess(n);
                } else {
                    firstTextNode = null;
                }
            }
            return node;
        }

        private void setCTD() {
            double a = 1.0 * Math.max(characters, 1) * Math.max(linkCharacters, 1) / Math.max(characters - linkCharacters, 1);
            double b = 1.0 * Math.max(characters, 1) * Math.max(linkCharacters, 1) / Math.max(characters, 1);
            double base = Math.log(a + b + Math.E);
            double antilog = 1.0 * Math.max(characters, 1) * Math.max(tags, 1) / Math.max(linkCharacters, 1) / Math.max(linkTags, 1);
            compositeTextDensity = textDensity * (Math.log(antilog) / Math.log(base));
        }

        private void setDensitySum() {
            if (node.childNodes() != null && node.childNodeSize() > 0) {
                densitySum = children.stream().mapToDouble((n) -> n.compositeTextDensity).summaryStatistics().getSum();
            } else {
                densitySum = compositeTextDensity;
            }
        }

        private MyNode getMaxDensitySumNode() {
            double maxDensitySum = Double.MIN_VALUE;
            MyNode maxDensitySumNode = this;
            Queue<MyNode> nodes = new LinkedList<>();
            nodes.add(this);
            while (nodes.peek() != null) {
                MyNode node = nodes.poll();
                if (node.densitySum > maxDensitySum) {
                    maxDensitySum = node.densitySum;
                    maxDensitySumNode = node;
                }
                if (node.children != null) {
                    node.children.forEach((n) -> nodes.add(n));
                }
            }
            return maxDensitySumNode;
        }

        public double markContent() {
            markContentDFS(this);
            MyNode node = getMaxDensitySumNode();
            List<MyNode> path = node.getPath();
            threshold = path.stream().mapToDouble(n -> n.compositeTextDensity).summaryStatistics().getMin();
            markContentRecursively();
            return threshold;
        }

        private void markContentRecursively() {
            if (children != null) {
                children.forEach(n -> {
                    n.markContentRecursively();
                });
            }
            if (compositeTextDensity > threshold) {
                MyNode bestChild = getNodeWithBestDensitySum();
                if (bestChild != null) {
                    bestChild.isContent = true;
                }
            }
        }

        private MyNode getNodeWithBestDensitySum() {
            MyNode node = null;
            double bestDensitySum = Double.MIN_VALUE;
            if (children != null) {
                for (MyNode n : children) {
                    if (!n.isTextNode() && n.densitySum >= bestDensitySum) {
                        bestDensitySum = n.densitySum;
                        node = n;
                    }
                }
            }
            return node;
        }

        private List<MyNode> getPath() {
            List<MyNode> list = new ArrayList<>();
            MyNode cur = this;
            while (cur.parent != null) {
                list.add(cur);
                cur = cur.parent;
            }
            list.add(cur);
            return list;
        }

        private void markContentDFS(MyNode node) {
            if (node.children != null) {
                node.children.forEach((n) -> {
                    markContentDFS(n);
                });
            }
            node.setCTD();
            node.setDensitySum();
        }

    }


    public static void main(String[] args) {
        DataExtraction extraction = new DataExtraction();
        System.out.println(extraction.extractStructureNewsInfo(RequestAndResponseTool.sendRequstAndGetResponse("http://news.china.com/socialgd/10000169/20180403/32268207.html").getHtml(), "http://news.china.com/socialgd/10000169/20180403/32268207.html", "sina"));
//        Document document = Jsoup.parse("<html><body><div><div><div><div><div><div><p>test-test</p></div></div></div></div></div></div></body></html>");
//        Elements elements = document.getElementsMatchingText("test");
//        System.out.println(elements.size());
    }

}
