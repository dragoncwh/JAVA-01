package cwh.week5.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE, classes = {PreparedStatementApplication.class, HikariApplication.class}))
public class JDBCApplication implements CommandLineRunner {

  private  static final Logger log = LoggerFactory.getLogger(JDBCApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(JDBCApplication.class, args);
  }

  @Value("${spring.datasource.driverClassName}")
  private String driver;
  @Value("${spring.datasource.url}")
  private String url;
  @Value("${spring.datasource.username}")
  private String userName;
  @Value("${spring.datasource.password}")
  private String password;

  @Override
  public void run(String... args) throws Exception {
    try {
      log.info("Creating table");

      // DROP OLD TABLE
      String drop = "DROP TABLE students IF EXISTS";
      Statement statement = getStatement();
      statement.execute(drop);

      // CREATE TABLE
      String create = "CREATE TABLE students(" +
          "id INT AUTO_INCREMENT, name VARCHAR(255) )";
      statement.execute(create);

      List<Student> students =
          Arrays.asList("Jenny", "Molly", "Jared", "George")
              .stream()
              .map(name -> new Student(name))
              .collect(Collectors.toList());

      // INSERT
      students.forEach(s -> log.info(String.format("Inserting student record for %s", s)));
      for (Student student : students) {
        String insert = "INSERT INTO students (name) VALUES ('" + student.getName() + "')";
        statement.execute(insert);
      }

      // QUERY
      String query = "SELECT * FROM students";
      ResultSet resultSet = statement.executeQuery(query);
      students = new ArrayList<>();
      while (resultSet.next()) {
        Student s = new Student(resultSet.getInt(1), resultSet.getString(2));
        students.add(s);
      }
      log.info("All students: {}", students);

      // UPDATE
      int id2Update = 1;
      String update = "UPDATE students set name = " + "'Adam'" + " WHERE  id = " + id2Update + "";
      int updatedCount = statement.executeUpdate(update);
      if (updatedCount != 1) {
        log.error("Failed to update student record with id : {}", id2Update);
      }

      // DELETE
      int id2Delete = 2;
      String delete = "DELETE FROM students WHERE id = " + id2Delete;
      int deleteCount = statement.executeUpdate(delete);
      if (deleteCount != 1) {
        log.error("Failed to update student record with id : {}", id2Delete);
      }

      // QUERY
      resultSet = statement.executeQuery(query);
      students = new ArrayList<>();
      while (resultSet.next()) {
        Student s = new Student(resultSet.getInt(1), resultSet.getString(2));
        students.add(s);
      }
      log.info("Updated students: {}", students);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private Connection getConnection() throws ClassNotFoundException, SQLException {
    Class.forName(driver);
    return DriverManager.getConnection(url, userName, password);
  }

  private Statement getStatement() throws ClassNotFoundException, SQLException {
    return getConnection().createStatement();
  }
}
