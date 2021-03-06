package cwh.java.geektime.datasourcesplitshardingjdbc.student.repository;

import cwh.java.geektime.datasourcesplitshardingjdbc.student.domain.Student;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.springframework.stereotype.Component;

@Component
public class StudentRepositoryImpl implements StudentRepository {

  @Resource
  private DataSource dataSource;

  @Override
  public boolean insert(Student student) throws Exception {
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      Statement statement = connection.createStatement();
      String insert = "INSERT INTO students(name) VALUES ('" + student.getName() + "')";
      statement.execute(insert);
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    } finally {
      if (connection != null) {
        connection.close();
      }
    }
    return true;
  }

  @Override
  public Collection<Student> getAll() throws Exception {
    Connection connection = null;
    List<Student> students = new ArrayList<>();
    try {
      connection = dataSource.getConnection();

      PreparedStatement selectStmt = connection.prepareStatement("SELECT id,name FROM students");
      ResultSet resultSet = selectStmt.executeQuery();
      while (resultSet.next()) {
        Student s = new Student();
        s.setId(resultSet.getLong("id"));
        s.setName(resultSet.getString("name"));
        students.add(s);
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    } finally {
      if (connection != null) {
        connection.close();
      }
    }
    return students;
  }
}
