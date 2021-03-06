package cwh.java.geektime.datasourcesplit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {

  private static final Logger log = LoggerFactory.getLogger(DynamicDataSource.class);

  @Override
  protected Object determineCurrentLookupKey() {
    log.info("Current DataSource is {}", DynamicDataSourceContext.getDataSourceType());
    return DynamicDataSourceContext.getDataSourceType();
  }
}
