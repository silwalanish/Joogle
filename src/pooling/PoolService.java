package pooling;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

public abstract class PoolService<T extends IPoolable, R> {

  protected final int poolSize;
  private ConcurrentLinkedDeque<T> pool;
  private ConcurrentLinkedDeque<T> runningObjs;

  public PoolService(int poolSize) {
    this.poolSize = poolSize;
    this.pool = new ConcurrentLinkedDeque<>();
    this.runningObjs = new ConcurrentLinkedDeque<>();
  }

  public abstract void initPool();

  public abstract void putResult(R result);

  public void addInstance(T obj) throws PoolOverflowException {
    if (pool.size() > poolSize){
      throw new PoolOverflowException("Pool Size Exceeded. Cannot add new instance.");
    }
    pool.add(obj);
  }

  public T aquirePooledInstance() {
    while (true) {
      if (!pool.isEmpty()) {
        try {
          T freeObj = pool.poll();
          freeObj.reset();
          runningObjs.add(freeObj);
          return freeObj;
        } catch (NullPointerException e) {
          System.out.println(pool.size());
        }
      }

      try {
        TimeUnit.MILLISECONDS.sleep(20);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public void completed(T obj) throws PoolOverflowException {
    runningObjs.removeFirstOccurrence(obj);
    addInstance(obj);
  }

  public void stopped(T obj) {
    if (obj.shouldExit()) {
      pool.remove(obj);
    }
  }

  public abstract void run(T obj);

  public void cleanup() {
    for (T obj : runningObjs) {
      obj.exit();
    }
    for (T obj : pool) {
      obj.exit();
      pool.remove(obj);
      postCleanUp(obj);
    }
    for (T obj: runningObjs) {
      runningObjs.remove(obj);
      postCleanUp(obj);
    }
  }

  public abstract void postCleanUp(T obj);

  public final int getPoolSize() {
    return poolSize;
  }

  public final int getNumRunningObjs() {
    return runningObjs.size();
  }

  public final int getNumPooledObjs() {
    return poolSize - runningObjs.size();
  }

}
