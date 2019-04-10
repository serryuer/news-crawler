package com.bishe.ana;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SimilarityCal {

    //计算cosine
    public static double calSimilarity(Map<String, Double> words1, Map<String, Double> words2) {
        double a = 0.0, b = 0.0;
        final double[] c = {0.0};
        for (String s : words1.keySet()) {
            a += (words1.get(s) * words1.get(s));
        }
        for (String s : words2.keySet()) {
            b += Math.pow(words2.get(s), 2);
        }
        words1.forEach((word, tfidf) -> {
            if (words2.containsKey(word)) {
                c[0] += words1.get(word) * words2.get(word);
            }
        });
        double similarity = c[0] / (Math.sqrt(a) * Math.sqrt(b));
        if (words1.size() > 5 && words2.size() > 5) {
            List<String> w1 = words1.entrySet().stream().sorted(Map.Entry.<String, Double>comparingByValue().reversed()).collect(Collectors.toList()).subList(0, 5).stream().map(entry -> entry.getKey()).collect(Collectors.toList());
            List<String> w2 = words2.entrySet().stream().sorted(Map.Entry.<String, Double>comparingByValue().reversed()).collect(Collectors.toList()).subList(0, 5).stream().map(entry -> entry.getKey()).collect(Collectors.toList());
            int repeat = 0;
            for (String s : w1) {
                if (w2.contains(s)) {
                    repeat++;
                }
            }
            if (repeat > 2) {
                similarity += 0.1;
            }
        }
        return similarity;
    }

}
