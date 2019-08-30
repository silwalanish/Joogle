package orm;

import com.anish.pooling.PoolOverflowException;
import com.anish.pooling.PoolService;

public class DBConnectionPooler extends PoolService<DBConnection, Object> {

  private static final DBConnectionPooler INSTANCE = new DBConnectionPooler(20);

  public static DBConnection getConnection() {
    return INSTANCE.aquirePooledInstance();
  }

  private DBConnectionPooler(int poolSize) {
    super(poolSize);
    initPool();
  }

  @Override
  public void initPool() {
    for (int i = 0; i < poolSize; i++){
      try {
        addInstance(new DBConnection(this));
      } catch (PoolOverflowException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void putResult(Object result) {
    // Ignore
  }

  @Override
  public void run(DBConnection obj) {
    // Ignore
  }

  @Override
  public void postCleanUp(DBConnection obj) {
    // Ignore
  }

}
