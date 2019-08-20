import webcrawler.WebCrawler;
import webcrawler.WebCrawlerPooler;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.TimeUnit;

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
    long startTime = System.currentTimeMillis();
    init();
    run();
    cleanup();

    pooler.showDetails();
    System.out.println("TOTAL TIME: " + (System.currentTimeMillis() - startTime) + "ms.");
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

    file.close();

    return urlBuffer.toString().split("\n");
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

    while (pooler.getNumStoppedObjs() != pooler.getPoolSize()){
      try {
        TimeUnit.MILLISECONDS.sleep(20);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

}
