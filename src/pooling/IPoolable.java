package pooling;

public interface IPoolable<T extends IPoolable, R> {

  void reset();

  void exit();

  boolean isRunning();
  boolean ready();
  boolean shouldExit();

  PoolService<T, R> getPoolService();

}
