package orm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBTable extends QueryBuilder<Map<String, Object>> {

  private String tableName;
  private List<String> columns;

  public DBTable(String tableName, List<String> columns) {
    super();
    this.tableName = tableName;
    this.columns = columns;
  }

  public void addColumn(String columnName) {
    columns.add(columnName);
  }

  public List<String> getColumns() {
    return new ArrayList<>(columns);
  }

  @Override
  protected String getTableName() {
    return tableName;
  }

  @Override
  protected Map<String, Object> parseRow(Map<String, Object> row) {
    HashMap<String, Object> data = new HashMap<>();

    for (String column: columns) {
      data.put(column, row.getOrDefault(column, null));
    }

    return data;
  }

}
