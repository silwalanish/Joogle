package pooling;

public interface IPoolable<T extends IPoolable> {

  void reset();

  void exit();

  boolean isRunning();
  boolean hasCompleted();
  boolean ready();
  boolean shouldExit();


  PoolService<T> getPoolService();

}
