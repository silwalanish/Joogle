import webcrawler.WebCrawler;
import webcrawler.WebCrawlerPooler;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Joogle {

  private String[] keywords;
  private String urlFilePath;
  private int poolSize;
  private WebCrawlerPooler pooler;

  public Joogle(String[] keywords, String urlFilePath, int poolSize) {
    this.keywords = keywords;
    this.urlFilePath = urlFilePath;
    this.poolSize = poolSize;

    pooler = new WebCrawlerPooler(poolSize);
  }

  public void start () throws IOException {
    init();
    run();
    cleanup();
  }

  protected void init () {
    pooler.initPool();
  }

  protected String[] readUrls() throws IOException {
    StringBuffer urlBuffer = new StringBuffer();
    InputStream file = new FileInputStream(urlFilePath);
    int chr;
    while ((chr = file.read()) != -1){
      urlBuffer.append((char) chr);
    }

    String[] urls = urlBuffer.toString().split("\n");
    return urls;
  }

  protected void run() throws IOException {
    String [] urls = readUrls();

    for (String url: urls){
      WebCrawler webCrawler = pooler.getInstance();
      webCrawler.init(new URL(url), keywords);
      pooler.run(webCrawler);
    }
  }

  protected void cleanup() {
    pooler.cleanup();
  }

}
