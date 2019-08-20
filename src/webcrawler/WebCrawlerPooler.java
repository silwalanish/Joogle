package webcrawler;

import pooling.PoolOverflowException;
import pooling.PoolService;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class WebCrawlerPooler extends PoolService<WebCrawler> {

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
//    System.out.println("Max Pool Size: " + getPoolSize());
//    System.out.println("Running Objects: " + getNumObjRunning());
//    System.out.println("Pooled Objects: " + getNumObjPooled());

    if (!obj.isRunning()) {
      obj.interrupt();
    }
  }

  public void putResult(HashMap<String, Integer> result) {
    for (String keyword: result.keySet()) {
      analysis.put(keyword, analysis.getOrDefault(keyword, 0) + result.get(keyword));
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
