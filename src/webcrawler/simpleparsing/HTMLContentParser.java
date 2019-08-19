package webcrawler.simpleparsing;

public class HTMLContentParser {

  public static String parse(String html) {
    int bodyIndex = html.indexOf("<body>");

    return html.substring(bodyIndex)
            .strip()
            .replaceAll("\\s{2,}", " ")
            .replaceAll("<[^>]*>", "")
            .toLowerCase();
  }

}
