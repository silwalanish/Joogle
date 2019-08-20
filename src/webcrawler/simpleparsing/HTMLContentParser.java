package webcrawler.simpleparsing;

public class HTMLContentParser {

  public static String parse(String html) {
    html = html.toLowerCase();
    int bodyIndex = html.indexOf("<body>");
    if (bodyIndex != -1) {
      return html.substring(bodyIndex)
              .replaceAll("<[^>]*>", "");
    }else{
      return html.replaceAll("<[^>]*>", "");
    }
  }

}
