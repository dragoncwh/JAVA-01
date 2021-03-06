package cwh.java.geektime.l13w7;


import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;

public class Configuration {

  @Bean(name = "rawDatasource")
  @ConfigurationProperties(prefix = "spring.datasource.raw")
  public DataSource rawDataSource() {
    return DataSourceBuilder.create().build();
  }

  @Bean(name = "hikariDatasource")
  @ConfigurationProperties(prefix = "spring.datasource.Hikari")
  public DataSource hikariDataSource() {
    return DataSourceBuilder.create().build();
  }
}
