package webcrawler;

import models.OverallStats;
import pooling.PoolOverflowException;
import pooling.PoolService;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class WebCrawlerPooler extends PoolService<WebCrawler, HashMap<String, Integer>> {

  protected ConcurrentMap<String, Integer> analysis;

  public WebCrawlerPooler(int poolSize) {
    super(poolSize);
    analysis = new ConcurrentHashMap<>();
  }

  @Override
  public void initPool() {
    for (int i = 0; i < poolSize; i++) {
      try {
        WebCrawler obj = new WebCrawler(this);
        addInstance(obj);
        obj.start();
      } catch (PoolOverflowException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void run(WebCrawler obj) {

    if (!obj.isRunning()) {
      obj.interrupt();
    }
  }

  @Override
  public void postCleanUp(WebCrawler obj) {
    try {
      obj.join();
    } catch (InterruptedException e) {
      // Ignore
    }
  }

  @Override
  public void putResult(HashMap<String, Integer> result) {
    for (String keyword: result.keySet()) {
      analysis.put(keyword, analysis.getOrDefault(keyword, 0) + result.get(keyword));
    }
  }

  public void saveResult() {
    for (String key: analysis.keySet()) {
      OverallStats stats = new OverallStats().where("keyword", "=", key).first();
      if (stats != null) {
        stats.setCount(analysis.get(key));
      } else {
        stats = new OverallStats();
        stats.setKeyword(key);
        stats.setCount(analysis.get(key));
      }
      stats.save();
    }
  }

  public void showDetails() {
    StringBuilder detailOutput = new StringBuilder();
    detailOutput.append("----------------------------------\n");
    for (String key: analysis.keySet()) {
      detailOutput.append(key);
      detailOutput.append(" : ");
      detailOutput.append(analysis.getOrDefault(key, 0));
      detailOutput.append("\n");
    }
    detailOutput.append("----------------------------------\n\n");

    System.out.println(detailOutput);
  }

}
