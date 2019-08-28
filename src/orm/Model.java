package orm;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class Model extends QueryBuilder {

  private static final String ID_COLUMN = "id";

  protected int id;
  protected boolean saved = false;
  protected boolean changed = false;

  public Model() {
    id = -1;
    select();
  }

  public void save() {
    try {
      if (saved && changed) {
        update().where(getIdField(), "=", this.id).commit();
      } else if(!saved) {
        insert(getColumns(), Arrays.asList(asList()));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    this.changed = false;
  }

  @Override
  public QueryBuilder delete() {
    return super.delete().where(getIdField(), "=", this.id);
  }

  public String getIdField() {
    return ID_COLUMN;
  }

  public int getId() {
    return id;
  }

  @Override
  protected <T> T parseRow(Map<String, Object> row) {
    Model obj = parse(row);
    obj.saved = true;
    return (T) obj;
  }

  protected abstract Model parse(Map<String, Object> data);

  protected abstract List<Object> asList();

  protected abstract String[] getColumns();

  public <T> T find(int id) {
    return select().where(getIdField(), "=", id).first();
  }

}
