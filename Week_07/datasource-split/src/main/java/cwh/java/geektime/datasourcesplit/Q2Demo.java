package cwh.java.geektime.datasourcesplit;

import cwh.java.geektime.datasourcesplit.student.domain.Student;
import cwh.java.geektime.datasourcesplit.student.service.UserService;
import java.sql.Connection;
import java.sql.Statement;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@Import({DynamicDataSourceRegister.class})
@SpringBootApplication
public class Q2Demo implements CommandLineRunner {

  public static void main(String[] args) {
    // SpringApplication.run(Q2Demo.class, args);
    SpringApplication springApplication = new SpringApplication(Q2Demo.class);
    springApplication.setAdditionalProfiles("q2");
    springApplication.run(args);
  }

  @Autowired
  UserService service;

  @Override
  public void run(String... args) throws Exception {
    initDb();

    Student student1 = new Student();
    student1.setName("test_student_1");
    service.insert(student1);

    System.out.println(service.getAll());
    System.out.println(service.getAll());
    System.out.println(service.getAll());
    System.out.println(service.getAll());
    System.out.println(service.getAll());
  }

  private void initDb() throws Exception {
    initDs(dataSource1);
    initDs(dataSource2);
    initDs(dataSource3);
  }

  private void initDs(DataSource ds) throws Exception {
    Connection connection = null;
    try {
      connection = ds.getConnection();

      // DROP OLD TABLE
      String drop = "DROP TABLE IF EXISTS students";
      Statement statement = connection.createStatement();
      statement.execute(drop);

      // CREATE TABLE
      String create = "CREATE TABLE students(" +
          "id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255) )";
      statement.execute(create);

      String name = ds.toString();
      String insert = "INSERT INTO students(name) VALUES ('" + name + "')";
      statement.execute(insert);
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    } finally {
      if (connection != null) {
        connection.close();
      }
    }
  }

  @Bean(name="dsp1")
  @Primary
  @ConfigurationProperties("spring.datasource.test1")
  public DataSourceProperties dataSourceProperties1() {
    return new DataSourceProperties();
  }

  @Bean(name="dsp2")
  @ConfigurationProperties("spring.datasource.test2")
  public DataSourceProperties dataSourceProperties2() {
    return new DataSourceProperties();
  }

  @Bean(name="dsp3")
  @ConfigurationProperties("spring.datasource.test3")
  public DataSourceProperties dataSourceProperties3() {
    return new DataSourceProperties();
  }

  @Bean(name="test1")
  public DataSource dataSource1(@Autowired @Qualifier("dsp1") DataSourceProperties props) {
    return props.initializeDataSourceBuilder().build();
  }

  @Bean(name="test2")
  public DataSource dataSource2(@Autowired @Qualifier("dsp2") DataSourceProperties props) {
    return props.initializeDataSourceBuilder().build();
  }

  @Bean(name="test3")
  public DataSource dataSource3(@Autowired @Qualifier("dsp3") DataSourceProperties props) {
    return props.initializeDataSourceBuilder().build();
  }

  @Autowired
  @Qualifier("test1")
  private DataSource dataSource1;

  @Autowired
  @Qualifier("test2")
  private DataSource dataSource2;

  @Autowired
  @Qualifier("test3")
  private DataSource dataSource3;

}
