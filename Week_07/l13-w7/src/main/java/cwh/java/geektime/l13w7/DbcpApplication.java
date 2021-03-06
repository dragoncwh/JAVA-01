package cwh.java.geektime.l13w7;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
@ComponentScan(
		excludeFilters = @ComponentScan.Filter(
				type = FilterType.ASSIGNABLE_TYPE, classes = {DruidApplication.class, HikariApplication.class}))
public class DbcpApplication implements CommandLineRunner {

	private  static final Logger log = LoggerFactory.getLogger(DbcpApplication.class);

	@Bean
	@Primary
	@ConfigurationProperties("spring.datasource.dbcp")
	public DataSourceProperties dataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	public DataSource dataSource(@Autowired DataSourceProperties props) {
		return props.initializeDataSourceBuilder().build();
	}

	@Autowired
	private DataSource dataSource;

	public static void main(String[] args) {
		SpringApplication.run(DbcpApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		try {

			// int size = 1001;
			int size = 1_000_000;
			int batchSize = 1000;

			// Util.rawJDBCTest(dataSource, size);
			//
			// Util.preparedStatementTest(dataSource, size);
			//
			// Util.PreparedStatementAndBatchTest(dataSource, size, batchSize);

			Util.concurrentPreparedStatementAndBatchTest(dataSource, size, batchSize);

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.exit(0);
	}

	private Connection getConnection() throws ClassNotFoundException, SQLException {
		// Class.forName(driver);
		// return DriverManager.getConnection(url, userName, password);
		return dataSource.getConnection();
	}

	// private Statement getStatement() throws ClassNotFoundException, SQLException {
	// 	return getConnection().createStatement();
	// }
}