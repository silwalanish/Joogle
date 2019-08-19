package webcrawler;

import pooling.PoolOverflowException;
import pooling.PoolService;

public class WebCrawlerPooler extends PoolService<WebCrawler> {

  public WebCrawlerPooler(int poolSize) {
    super(poolSize);
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

}
