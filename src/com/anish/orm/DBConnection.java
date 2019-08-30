package orm;

import com.anish.pooling.IPoolable;
import com.anish.pooling.PoolService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection implements IPoolable<DBConnection, Object> {

  private final static String CONNECTION_STRING = "jdbc:postgresql://localhost/silwalanish?user=silwalanish&password=thisistest";

  private Connection connection;
  private final PoolService<DBConnection, Object> poolService;

  public DBConnection(DBConnectionPooler pooler) {
    poolService = pooler;
    try {
      connection = DriverManager.getConnection(CONNECTION_STRING);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public Connection getConnection(){
    return connection;
  }

  @Override
  public void reset() {
    // Ignore
  }

  @Override
  public void exit() {
    try {
      connection.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean isRunning() {
    return !shouldExit();
  }

  @Override
  public boolean ready() {
    try {
      return connection.isValid(0);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public boolean shouldExit() {
    try {
      return connection.isClosed();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return true;
  }

  @Override
  public PoolService<DBConnection, Object> getPoolService() {
    return poolService;
  }

}
