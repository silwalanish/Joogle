package webcrawler;

import pooling.IPoolable;
import pooling.PoolOverflowException;
import pooling.PoolService;
import webcrawler.simpleparsing.HTMLContentParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class WebCrawler extends Thread implements IPoolable {

  protected URL url;
  protected HashMap<String, Integer> freqs;
  protected String[] tags;
  protected long startTime;
  protected long endTime;

  private boolean isRunning;
  private boolean isReady;
  private boolean isCompleted;
  private boolean willExit;

  private PoolService<WebCrawler> poolService;

  public WebCrawler(PoolService<WebCrawler> poolService) {
    this.poolService = poolService;
    reset();
  }

  public void init(URL url, String[] tags){
    this.url = url;
    this.tags = tags;
    this.freqs = new HashMap<>();

    this.willExit = false;
    this.isCompleted = false;
    this.isRunning = false;

    this.isReady = true;
  }

  @Override
  public void run() {
    while (!shouldExit()) {
      if (ready()) {
        isReady = false;
        isRunning = true;

        try {
          HttpURLConnection conn = (HttpURLConnection) url.openConnection();
          conn.setRequestMethod("GET");

          startTime = System.currentTimeMillis();
          StringBuffer buffer = new StringBuffer();
          BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

          String line;
          while ((line = reader.readLine()) != null) {
            buffer.append(line);
          }

          String parsedContent = HTMLContentParser.parse(buffer.toString());
          this.freqs = (HashMap<String, Integer>) OccurrenceCounter.count(parsedContent, tags);

          endTime = System.currentTimeMillis();

          showDetails();
        } catch (MalformedURLException e) {
          System.out.println("URL is invalid: " + url.toString());
        } catch (IOException e) {
          System.out.println("Could not open URL: " + url.toString());
        } catch (Exception e) {
          System.out.println("Error : " + url.toString());
        }

        isRunning = false;
        isCompleted = true;

        try {
          getPoolService().completed(this);
        } catch (PoolOverflowException e) {
          e.printStackTrace();
        }
      }

      try {
        TimeUnit.SECONDS.sleep(2);
      } catch (InterruptedException e) {
        if (isInterrupted()) {
          isReady = true;
        }
      }
    }
  }

  public void showDetails() {
    String detailOutput = "";
    detailOutput += "----------------------------------\n";
    detailOutput += "Details of  " + url + "\n";
    for (String key: freqs.keySet()) {
      detailOutput += key + " : " + freqs.get(key) + "\n";
    }
    detailOutput += "Started at: " + startTime + "ms.." + "\n";
    detailOutput += "Finished at: " + endTime + "ms.." + "\n";
    detailOutput += "Time taken " + (endTime - startTime) + "ms.." + "\n";
    detailOutput += "----------------------------------\n\n";

    System.out.println(detailOutput);
  }

  @Override
  public void reset() {
    this.url = null;
    this.tags = null;
    this.freqs = new HashMap<>();

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
  public boolean hasCompleted() {
    return isCompleted;
  }

  @Override
  public boolean shouldExit() {
    return willExit;
  }

  @Override
  public PoolService getPoolService() {
    return poolService;
  }
}
