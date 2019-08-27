package orm;

import orm.utils.StringUtils;
import pooling.PoolOverflowException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public abstract class QueryBuilder {

  private StringBuilder query = new StringBuilder();

  protected abstract String getTableName();

  protected abstract <T> T parseRow(Map<String, Object> row);

  protected abstract Map<String, Object> serialize();

  public QueryBuilder select() {
    return select(null);
  }

  public QueryBuilder select(String[] columns) {
    query = new StringBuilder();
    query.append("SELECT ");

    if (columns == null || columns.length == 0) {
      query.append("* ");
    } else {
      query.append(StringUtils.flattenArray(columns));
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
    return orderBy(new String[] { column }, order);
  }

  public QueryBuilder orderBy(String[] columns, String order) {
    query.append("ORDER BY ");
    query.append(StringUtils.flattenArray(columns));
    query.append(order);

    return this;
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

  public <T> List<T> get() {
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

  public <T> T first() {
    List<T> result = get();
    if (result.size() > 0) {
      return (T) result.get(0);
    }
    return null;
  }

  public QueryBuilder update() {
    update(serialize());

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

  public int insert(String[] columns, List<List<Object>> values) {
    query = new StringBuilder();
    query.append("INSERT INTO ");
    query.append(getTableName());

    if (columns != null || columns.length > 0) {
      query.append("(");
      query.append(StringUtils.flattenArray(columns));
      query.append(")");
    }

    query.append(" VALUES ");

    int rowIndex = 0;
    int rowCount = values.size();
    for (List<Object> row: values) {
      query.append("(");
      query.append(StringUtils.singleQuoted(row));
      query.append(")");
      if(rowIndex < rowCount - 1) {
        query.append(", ");
      }
      rowIndex++;
    }

    return commit();
  }

  public QueryBuilder delete() {
    query = new StringBuilder();
    query.append("DELETE FROM ");
    query.append(getTableName());

    return this;
  }

  public int commit() {
    int count = 0;
    try {
      DBConnection conn = DBConnectionPooler.getConnection();
      count = conn.getConnection().createStatement().executeUpdate(query.toString());
      conn.getPoolService().completed(conn);
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (PoolOverflowException e) {
      e.printStackTrace();
    }
    return count;
  }

}
