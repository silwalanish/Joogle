package orm.utils;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class StringUtils {

  public static StringBuilder flattenArray(String[] array) {
    StringBuilder builder = new StringBuilder();
    int count = 0;
    int len = array.length;
    for (String column : array) {
      builder.append(column);
      if (count < len - 1) {
        builder.append(", ");
      } else {
        builder.append(" ");
      }
      count++;
    }
    return builder;
  }

  public static StringBuilder singleQuoted(List<Object> list) {
    StringBuilder builder = new StringBuilder();

    int valueIndex = 0;
    int len = list.size();
    for (Object value: list) {
      builder.append("'");
      builder.append(value);
      builder.append("'");
      if(valueIndex < len - 1){
        builder.append(", ");
      }
      valueIndex++;
    }

    return builder;
  }

  public static StringBuilder keyEqualsValue(Map<String, Object> values) {
    StringBuilder builder = new StringBuilder();

    Set<String> keySet = values.keySet();
    int keyCount = keySet.size();
    int count = 0;
    for (String column: keySet) {
      builder.append(column);
      builder.append(" = ");
      builder.append("'");
      builder.append(values.get(column));
      builder.append("'");
      if (count < keyCount - 1) {
        builder.append(", ");
      } else {
        builder.append(" ");
      }
      count++;
    }

    return builder;
  }

  public static StringBuilder whereCondition(String column, String operator, Object value) {
    StringBuilder builder = new StringBuilder();

    builder.append(column);
    builder.append(" ");
    builder.append(operator);
    builder.append(" '");
    builder.append(value);
    builder.append("' ");

    return builder;
  }

}
