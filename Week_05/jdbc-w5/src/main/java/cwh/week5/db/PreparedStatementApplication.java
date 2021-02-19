package cwh.week5.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE, classes = {JDBCApplication.class, HikariApplication.class}))
public class PreparedStatementApplication implements CommandLineRunner {

  private  static final Logger log = LoggerFactory.getLogger(PreparedStatementApplication.class);

  @Value("${spring.datasource.driverClassName}")
  private String driver;
  @Value("${spring.datasource.url}")
  private String url;
  @Value("${spring.datasource.username}")
  private String userName;
  @Value("${spring.datasource.password}")
  private String password;

  public static void main(String[] args) {
    SpringApplication.run(PreparedStatementApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    Connection connection = null;
    try {
      connection = getConnection();
      connection.setAutoCommit(false);
      log.info("Creating table");

      // DROP OLD TABLE
      PreparedStatement dropStmt = connection.prepareStatement("DROP TABLE students IF EXISTS");
      dropStmt.execute();

      // CREATE TABLE
      PreparedStatement createStmt = connection.prepareStatement("CREATE TABLE students(" +
          "id INT AUTO_INCREMENT, name VARCHAR(255) )");
      createStmt.execute();

      List<Student> students =
          Arrays.asList("Jenny", "Molly", "Jared", "George")
              .stream()
              .map(name -> new Student(name))
              .collect(Collectors.toList());

      // INSERT
      students.forEach(s -> log.info(String.format("Inserting student record for %s", s)));
      PreparedStatement insertStmt =
          connection.prepareStatement("INSERT INTO students (name) VALUES (?)");
      for (Student student : students) {
        insertStmt.setString(1, student.getName());
        insertStmt.addBatch();
      }

      insertStmt.executeBatch();

      // QUERY
      PreparedStatement selectStmt = connection.prepareStatement("SELECT * FROM students");
      ResultSet resultSet = selectStmt.executeQuery();
      students = new ArrayList<>();
      while (resultSet.next()) {
        Student s = new Student(resultSet.getInt(1), resultSet.getString(2));
        students.add(s);
      }
      log.info("All students: {}", students);

      // UPDATE
      int id2Update = 1;
      String newName = "Isma";
      PreparedStatement updateStmt = connection.prepareStatement(
          "UPDATE students set name = (?) WHERE id = (?)");
      updateStmt.setString(1, newName);
      updateStmt.setInt(2, id2Update);
      int updatedCount = updateStmt.executeUpdate();
      if (updatedCount != 1) {
        log.error("Failed to update student record with id : {}", id2Update);
      }

      // DELETE
      int id2Delete = 2;
      PreparedStatement deleteStmt = connection.prepareStatement(
          "DELETE FROM students WHERE id = (?)");
      deleteStmt.setInt(1, id2Delete);
      int deleteCount = deleteStmt.executeUpdate();
      if (deleteCount != 1) {
        log.error("Failed to update student record with id : {}", id2Delete);
      }

      // QUERY
      selectStmt = connection.prepareStatement("SELECT * FROM students");
      resultSet = selectStmt.executeQuery();
      students = new ArrayList<>();
      while (resultSet.next()) {
        Student s = new Student(resultSet.getInt(1), resultSet.getString(2));
        students.add(s);
      }
      log.info("Updated students: {}", students);

      connection.commit();
    } catch (Exception e) {
      e.printStackTrace();
      if (connection != null) {
        connection.rollback();
      }
    }
  }

  private Connection getConnection() throws ClassNotFoundException, SQLException {
    Class.forName(driver);
    return DriverManager.getConnection(url, userName, password);
  }
}
