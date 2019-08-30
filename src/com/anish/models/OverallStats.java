package models;

import orm.Model;

import java.sql.Timestamp;
import java.util.*;

public class OverallStats extends Model {

  private static final List<String> COLUMNS = Arrays.asList("keyword", "count", "created_at");
  private static final String TABLE_NAME = "ovrall_stats";

  private String keyword;
  private int count;
  private Timestamp created_at;

  public OverallStats() {
    this("", -1);
  }

  public OverallStats(String keyword, int count) {
    super();
    this.keyword = keyword;
    this.count = count;
    this.created_at = new Timestamp(System.currentTimeMillis());
  }

  public void setKeyword(String keyword) {
    this.changed = true;
    this.keyword = keyword;
  }

  public void setCount(int count) {
    this.changed = true;
    this.count = count;
  }

  public String getKeyword() {
    return keyword;
  }

  public int getCount() {
    return count;
  }

  public Timestamp getCreatedAt() {
    return created_at;
  }

  @Override
  protected Model parse(Map<String, Object> data) {
    OverallStats stats = new OverallStats();
    stats.id = (int) data.getOrDefault("id", 0);
    stats.keyword = (String) data.getOrDefault("keyword", "");
    stats.count = (int) data.getOrDefault("count", 0);
    stats.created_at = (Timestamp) data.getOrDefault("created_at", null);
    return stats;
  }

  @Override
  protected List<Object> asList() {
    ArrayList<Object> values = new ArrayList<>();
    values.add(keyword);
    values.add(count);
    values.add(created_at);
    return values;
  }

  @Override
  protected List<String> getColumns() {
    return COLUMNS;
  }

  @Override
  protected String getTableName() {
    return TABLE_NAME;
  }

  @Override
  protected Map<String, Object> serialize() {
    HashMap<String, Object> values = new HashMap<>();
    values.put("keyword", keyword);
    values.put("count", count);
    values.put("created_at", created_at);
    return values;
  }
}
