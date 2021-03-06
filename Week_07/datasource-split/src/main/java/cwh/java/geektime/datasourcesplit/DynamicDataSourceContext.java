package cwh.java.geektime.datasourcesplit;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DynamicDataSourceContext {
  private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

  public static List<String> dataSourceIds;

  private static final AtomicInteger counter = new AtomicInteger(0);

  public static void setDataSourceIdsIfEmpty(List<String> ids) {
    if (dataSourceIds == null || dataSourceIds.size() == 0) {
      synchronized (DynamicDataSourceContext.class) {
        if (dataSourceIds == null || dataSourceIds.size() == 0) {
          dataSourceIds = Collections.unmodifiableList(ids);
        }
      }
    }
  }

  public static void setDataSourceType(String dataSourceType) {
    contextHolder.set(dataSourceType);
  }

  public static String getDataSourceType() {
    return contextHolder.get();
  }

  public static void usePrimaryDataSource() {
    contextHolder.set(DynamicDataSourceType.PRIMARY.name());
  }

  public static void useSlaveDataSource() {
    int idx = counter.getAndIncrement() % dataSourceIds.size();
    contextHolder.set(dataSourceIds.get(idx));
  }

  public static void clear() {
    contextHolder.remove();
  }

  public static boolean contains(String dataSourceId) {
    return dataSourceIds.contains(dataSourceId);
  }
}
