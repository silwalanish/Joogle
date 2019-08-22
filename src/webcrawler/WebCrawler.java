package webcrawler;

import pooling.IPoolable;
import pooling.PoolOverflowException;
import pooling.PoolService;
import webcrawler.simpleparsing.HTMLContentParser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class WebCrawler extends Thread implements IPoolable<WebCrawler, HashMap<String, Integer>> {

  protected URL url;
  protected HashMap<String, Integer> freqs;
  protected String[] tags;
  protected HttpRequest request;

  protected long startTime;
  protected long endTime;

  private boolean isRunning;
  private boolean isReady;
  private boolean isCompleted;
  private boolean willExit;

  private PoolService<WebCrawler, HashMap<String, Integer>> poolService;

  public WebCrawler(PoolService<WebCrawler, HashMap<String, Integer>> poolService) {
    this.poolService = poolService;
    this.willExit = false;
    reset();
  }

  public void init(URL url, String[] tags){
    this.url = url;
    this.tags = tags;
    this.freqs = new HashMap<>();
    this.request = new HttpRequest(this.url);

    this.isCompleted = false;
    this.isRunning = false;

    this.isReady = true;
  }

  protected void analyze(String response) {
    String parsedContent = HTMLContentParser.parse(response);
    this.freqs = (HashMap<String, Integer>) OccurrenceCounter.count(parsedContent, tags);
  }

  @Override
  public void run() {
    while (!shouldExit()) {
      if (ready()) {
        isReady = false;
        isRunning = true;
        isCompleted = false;
        try {
          request.connect();
          startTime = System.currentTimeMillis();
          analyze(request.getResponse());
          endTime = System.currentTimeMillis();
          request.disconnect();

          getPoolService().putResult(freqs);
        } catch (MalformedURLException e) {
          System.out.println("URL is invalid: " + url.toString());
        } catch (IOException e) {
          System.out.println("Could not open URL: " + url.toString());
        } catch (Exception e) {
          System.out.println("Error : " + url.toString());
        }
        setCompleted();
      }
      if (!isInterrupted()) {
        try {
          TimeUnit.MILLISECONDS.sleep(20);
        } catch (InterruptedException e) {
          if (isInterrupted()) {
            isReady = true;
          }
        }
      } else {
        isReady = true;
      }
    }
    if (!isCompleted) {
      setCompleted();
    }
    getPoolService().stopped(this);
  }

  protected void setCompleted() {
    isRunning = false;
    isCompleted = true;

    try {
      getPoolService().completed(this);
    } catch (PoolOverflowException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void reset() {
    this.url = null;
    this.tags = null;
    this.freqs = new HashMap<>();
    this.request = null;

    this.isCompleted = false;
    this.isRunning = false;

    this.isReady = false;
  }

  @Override
  public boolean ready() {
    return isReady;
  }

  @Override
  public void exit() {
    willExit = true;
  }

  @Override
  public boolean isRunning() {
    return isRunning;
  }

  @Override
  public boolean shouldExit() {
    return willExit;
  }

  @Override
  public PoolService<WebCrawler, HashMap<String, Integer>> getPoolService() {
    return poolService;
  }

}
