package cwh.java.geektime.datasourcesplitshardingjdbc;

import cwh.java.geektime.datasourcesplitshardingjdbc.student.domain.Student;
import cwh.java.geektime.datasourcesplitshardingjdbc.student.service.UserService;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Q3Demo implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication springApplication = new SpringApplication(Q3Demo.class);
    springApplication.setAdditionalProfiles("q3");
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
    initDs("test1", driver1, url1, userName1, password1);
    initDs("test2", driver2, url2, userName2, password2);
    initDs("test3", driver3, url3, userName3, password3);
  }

  private void initDs(String studentName, String driverClassName, String url, String username, String password) throws Exception {
    Connection connection = null;
    try {
      connection = getConnection(driverClassName, url, username, password);

      // DROP OLD TABLE
      String drop = "DROP TABLE IF EXISTS students";
      Statement statement = connection.createStatement();
      statement.execute(drop);

      // CREATE TABLE
      String create = "CREATE TABLE students(" +
          "id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255) )";
      statement.execute(create);

      String insert = "INSERT INTO students(name) VALUES ('" + studentName + "')";
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


  @Value("${spring.datasource.test1.driverClassName}")
  private String driver1;
  @Value("${spring.datasource.test1.url}")
  private String url1;
  @Value("${spring.datasource.test1.username}")
  private String userName1;
  @Value("${spring.datasource.test1.password}")
  private String password1;

  @Value("${spring.datasource.test2.driverClassName}")
  private String driver2;
  @Value("${spring.datasource.test2.url}")
  private String url2;
  @Value("${spring.datasource.test2.username}")
  private String userName2;
  @Value("${spring.datasource.test2.password}")
  private String password2;

  @Value("${spring.datasource.test3.driverClassName}")
  private String driver3;
  @Value("${spring.datasource.test3.url}")
  private String url3;
  @Value("${spring.datasource.test3.username}")
  private String userName3;
  @Value("${spring.datasource.test3.password}")
  private String password3;

  private Connection getConnection(String driverClassName, String url, String username, String password) throws ClassNotFoundException, SQLException {
    Class.forName(driverClassName);
    return DriverManager.getConnection(url, username, password);
  }


  // @Bean(name="dsp1")
  // @Primary
  // @ConfigurationProperties("spring.datasource.test1")
  // public DataSourceProperties dataSourceProperties1() {
  //   return new DataSourceProperties();
  // }
  //
  // @Bean(name="dsp2")
  // @ConfigurationProperties("spring.datasource.test2")
  // public DataSourceProperties dataSourceProperties2() {
  //   return new DataSourceProperties();
  // }
  //
  // @Bean(name="dsp3")
  // @ConfigurationProperties("spring.datasource.test3")
  // public DataSourceProperties dataSourceProperties3() {
  //   return new DataSourceProperties();
  // }
  //
  // @Autowired
  // @Qualifier("dsp1")
  // DataSourceProperties props1;
  //
  // @Autowired
  // @Qualifier("dsp2")
  // DataSourceProperties props2;
  //
  // @Autowired
  // @Qualifier("dsp3")
  // DataSourceProperties props3;
  //
  // public DataSource dataSource1(DataSourceProperties props) {
  //   return props.initializeDataSourceBuilder().build();
  // }
  //
  // public DataSource dataSource2(DataSourceProperties props) {
  //   return props.initializeDataSourceBuilder().build();
  // }
  //
  // public DataSource dataSource3(DataSourceProperties props) {
  //   return props.initializeDataSourceBuilder().build();
  // }
  //
  // private DataSource dataSource1 = dataSource1(props1);
  //
  // private DataSource dataSource2 = dataSource2(props2);
  //
  // private DataSource dataSource3 = dataSource3(props3);

}
