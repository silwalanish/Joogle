package com.anish.webcrawler.simpleparsing;

public class HTMLContentParser {

  public static String parse(String html) {
    html = html.toLowerCase();
    int bodyIndex = html.indexOf("<body>");
    if (bodyIndex != -1) {
      return html.substring(bodyIndex)
              .replaceAll("<[^>]*>", "")
              .replaceAll("\\s{2,}", " ");
    }else{
      return html.replaceAll("<[^>]*>", "")
              .replaceAll("\\s{2,}", " ");
    }
  }

}
