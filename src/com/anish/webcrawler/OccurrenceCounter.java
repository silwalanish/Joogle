package com.anish.webcrawler;

import java.util.HashMap;
import java.util.Map;

public class OccurrenceCounter {

  public static Map<String, Integer> count(String text, String[] keywords) {
    Map<String, Integer> frequencies = new HashMap<>();

    for (String keyword: keywords) {
      if (keyword.isEmpty()) {
        continue;
      }
      keyword = keyword.toLowerCase();
      frequencies.put(keyword, countOccurrenceOf(text, keyword));
    }

    return frequencies;
  }

  protected static int countOccurrenceOf(String text, String keyword) {
    int textLen = text.length();
    int textLenWithNoKeyword = text.replaceAll(keyword, "").length();

    return (textLen - textLenWithNoKeyword) / keyword.length();
  }

}
