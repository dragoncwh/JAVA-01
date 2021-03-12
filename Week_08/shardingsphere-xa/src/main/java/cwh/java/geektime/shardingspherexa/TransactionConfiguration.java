package cwh.java.geektime.shardingspherexa;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class TransactionConfiguration {
  @Bean
  public PlatformTransactionManager txManager(@Autowired final DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }

  @Bean
  public JdbcTemplate jdbcTemplate(@Autowired final DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }
}
