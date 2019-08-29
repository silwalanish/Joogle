package models;

import orm.Model;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.*;

public class UrlStats extends Model {

  private static final List<String> COLUMNS = Arrays.asList("url", "keyword", "count", "created_at");
  private static final String TABLE_NAME = "url_stats";

  private URL url;
  private String keyword;
  private int count;
  private Timestamp created_at;

  public UrlStats() {
    this(null, "", -1);
  }

  public UrlStats(URL url, String keyword, int count) {
    super();
    this.url = url;
    this.keyword = keyword;
    this.count = count;
    this.created_at = new Timestamp(System.currentTimeMillis());
  }

  public void setUrl(URL url) {
    this.changed = true;
    this.url = url;
  }

  public void setKeyword(String keyword) {
    this.changed = true;
    this.keyword = keyword;
  }

  public void setCount(int count) {
    this.changed = true;
    this.count = count;
  }

  public URL getUrl() {
    return url;
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
    UrlStats stats = new UrlStats();
    stats.id = (int) data.getOrDefault("id", 0);
    try {
      stats.url = new URL((String) data.getOrDefault("url", ""));
    } catch (MalformedURLException e) {
      stats.url = null;
    }
    stats.keyword = (String) data.getOrDefault("keyword", "");
    stats.count = (int) data.getOrDefault("count", 0);
    stats.created_at = (Timestamp) data.getOrDefault("created_at", null);
    return stats;
  }

  @Override
  protected List<Object> asList() {
    ArrayList<Object> values = new ArrayList<>();
    values.add(url);
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
    values.put("url", url);
    values.put("keyword", keyword);
    values.put("count", count);
    values.put("created_at", created_at);
    return values;
  }

}
