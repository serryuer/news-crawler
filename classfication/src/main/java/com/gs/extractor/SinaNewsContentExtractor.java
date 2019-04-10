package com.gs.extractor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SinaNewsContentExtractor implements ContentExtractor {


	@Override
	public String extractFromHtml(String html) {
		String regex = "<div class=\"Main clearfix\">(.*?)<div class=\"show_author\">";
		Pattern pt = Pattern.compile(regex,Pattern.DOTALL);
		Matcher mt = pt.matcher(html);
		String re = null;
		if (mt.find()) {
			re = mt.group(1);
		}
		Pattern pt1 = Pattern.compile("<p>(.*?)</p>",Pattern.DOTALL);
		Matcher mt1 = pt1.matcher(re);
		re = "";
		while (mt1.find()) {
			re += mt1.group(1);
		}
		re = re.replaceAll("<.*?>", "");//抹掉所有尖括号的内容
		re = re.replaceAll("\\s", "");//抹掉所有空白
		return re;
	}

}
