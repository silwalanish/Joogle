package pooling;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public abstract class PoolService<T extends IPoolable> {

  protected final int poolSize;
  private LinkedList<T> pool;
  private LinkedList<T> runningObjs;

  public PoolService(int poolSize) {
    this.poolSize = poolSize;
    this.pool = new LinkedList<T>();
    this.runningObjs = new LinkedList<T>();
  }

  public abstract void initPool() throws PoolOverflowException;

  public void addInstance(T obj) throws PoolOverflowException {
    if (pool.size() > poolSize){
      throw new PoolOverflowException("Pool Size Exceeded. Cannot add new instance.");
    }
    pool.add(obj);
  }

  public T getInstance() {
    while (true) {
      if (!pool.isEmpty()) {
        T freeObj = pool.poll();
        freeObj.reset();
        runningObjs.add(freeObj);
        return freeObj;
      }

      try {
        TimeUnit.MILLISECONDS.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public void completed(T obj) throws PoolOverflowException {
    runningObjs.removeFirstOccurrence(obj);
    obj.reset();
    addInstance(obj);
  }

  public abstract void run(T obj);

  public void cleanup() {
    for (T obj: pool) {
      obj.exit();
    }
    for (T obj: runningObjs) {
      obj.exit();
    }
  }

  public final int getPoolSize() {
    return poolSize;
  }

  public final int getNumObjRunning() {
    return runningObjs.size();
  }

  public final int getNumObjPooled() {
    return poolSize - runningObjs.size();
  }

}
