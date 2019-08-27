package models;

import orm.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User extends Model {

  private static final String[] COLUMNS =  new String[] { "name", "email" };
  private static final String TABLE_NAME = "test";

  private String name;
  private String email;

  public User() {
    this("", "");
  }

  public User(String name, String email) {
    super();
    this.name = name;
    this.email = email;
  }

  @Override
  protected String getTableName() {
    return TABLE_NAME;
  }

  @Override
  protected Model parse(Map<String, Object> row) {
    User user = new User();
    user.id = (int) row.getOrDefault("id", 0);
    user.name = (String) row.getOrDefault("name", "");
    user.email = (String) row.getOrDefault("email", "");
    return user;
  }

  @Override
  protected List<Object> asList() {
    ArrayList<Object> values = new ArrayList<>();
    values.add(name);
    values.add(email);
    return values;
  }

  @Override
  protected String[] getColumns() {
    return COLUMNS;
  }

  @Override
  protected Map<String, Object> serialize() {
    HashMap<String, Object> values = new HashMap<>();
    values.put("name", name);
    values.put("email", email);
    return values;
  }

  public void setName(String name) {
    this.changed = true;
    this.name = name;
  }

  public void setEmail(String email) {
    this.changed = true;
    this.email = email;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

}
