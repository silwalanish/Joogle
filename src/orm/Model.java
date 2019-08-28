package orm;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class Model extends QueryBuilder {

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
        update().where("id", "=", this.id).commit();
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
    return super.delete().where("id", "=", this.id);
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
    return select().where("id", "=", id).first();
  }

}
