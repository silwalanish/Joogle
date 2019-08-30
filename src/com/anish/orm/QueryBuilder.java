package orm;

import orm.utils.StringUtils;
import com.anish.pooling.PoolOverflowException;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public abstract class QueryBuilder<T> {

  private StringBuilder query = new StringBuilder();

  public QueryBuilder select() {
    return select(null);
  }

  public QueryBuilder select(List<String> columns) {
    query = new StringBuilder();
    query.append("SELECT ");

    if (columns == null || columns.size() == 0) {
      query.append("* ");
    } else {
      query.append(StringUtils.flattenList(columns));
    }

    query.append("FROM ");
    query.append(getTableName());

    return this;
  }

  public QueryBuilder where(String column, String operator, Object value) {
    query.append(" WHERE ");

    query.append(StringUtils.whereCondition(column, operator, value));

    return this;
  }

  public QueryBuilder andWhere(String column, String operator, Object value) {
    query.append(" AND ");

    query.append(StringUtils.whereCondition(column, operator, value));

    return this;
  }

  public QueryBuilder orWhere(String column, String operator, Object value) {
    query.append(" OR ");

    query.append(StringUtils.whereCondition(column, operator, value));

    return this;
  }

  public QueryBuilder limit(long count) {
    return limit(count, -1);
  }

  public QueryBuilder limit(long count, long offset) {
    query.append(" LIMIT ");
    query.append(count);
    if (offset >= 0) {
      query.append(" OFFSET ");
      query.append(offset);
    }

    return this;
  }

  public QueryBuilder orderBy(String column) {
    return orderBy(column, "ASC");
  }

  public QueryBuilder orderBy(String column, String order) {
    return orderBy(Arrays.asList(column ), order);
  }

  public QueryBuilder orderBy(List<String> columns, String order) {
    query.append("ORDER BY ");
    query.append(StringUtils.flattenList(columns));
    query.append(order);

    return this;
  }

  public QueryBuilder update(Map<String, Object> values) {
    query = new StringBuilder();

    query.append("UPDATE ");
    query.append(getTableName());
    query.append(" SET ");

    query.append(StringUtils.keyEqualsValue(values));

    return this;
  }

  public QueryBuilder delete() {
    query = new StringBuilder();
    query.append("DELETE FROM ");
    query.append(getTableName());

    return this;
  }

  public int insert(List<String> columns, List<Map<String, Object>> values) throws SQLException {
    query = new StringBuilder();
    query.append("INSERT INTO ");
    query.append(getTableName());

    if (columns != null || columns.size() > 0) {
      query.append("(");
      query.append(StringUtils.flattenList(columns));
      query.append(")");
    }

    query.append(" VALUES ");

    int rowIndex = 0;
    int rowCount = values.size();
    for (Map<String, Object> row: values) {
      query.append("(");
      query.append(StringUtils.insertValueFormat(columns, row));
      query.append(")");
      if(rowIndex < rowCount - 1) {
        query.append(", ");
      }
      rowIndex++;
    }

    return commit();
  }

  protected ResultSet run() throws SQLException {
    DBConnection conn = DBConnectionPooler.getConnection();
    ResultSet set = conn.getConnection().createStatement().executeQuery(query.toString());
    try {
      conn.getPoolService().completed(conn);
    } catch (PoolOverflowException e) {
      e.printStackTrace();
    }

    return set;
  }

  public List<T> get() {
    List<T> result = new LinkedList<>();
    try {
      ResultSet queryResult = run();
      ResultSetMetaData meta = queryResult.getMetaData();
      int columnCount = meta.getColumnCount();
      while (queryResult.next()) {
        HashMap<String, Object> row = new HashMap<>();
        for (int i = 1; i <= columnCount; i++) {
          row.put(meta.getColumnName(i), queryResult.getObject(i));
        }
        result.add(parseRow(row));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return result;
  }

  public T first() {
    List<T> result = get();
    if (result.size() > 0) {
      return result.get(0);
    }
    return null;
  }

  public int commit() throws SQLException {
    int count = 0;
    try {
      DBConnection conn = DBConnectionPooler.getConnection();
      count = conn.getConnection().createStatement().executeUpdate(query.toString());
      conn.getPoolService().completed(conn);
    } catch (PoolOverflowException e) {
      e.printStackTrace();
    }
    return count;
  }

  protected abstract String getTableName();

  protected abstract T parseRow(Map<String, Object> row);

}
