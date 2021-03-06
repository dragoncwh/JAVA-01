package cwh.java.geektime.datasourcesplit.student.repository;

import cwh.java.geektime.datasourcesplit.DynamicDataSourceType;
import cwh.java.geektime.datasourcesplit.TargetDataSource;
import cwh.java.geektime.datasourcesplit.student.domain.Student;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StudentRepositoryImpl implements StudentRepository {

  @Autowired
  private DataSource dataSource;

  @Override
  @TargetDataSource(type = DynamicDataSourceType.PRIMARY)
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
  @TargetDataSource(type = DynamicDataSourceType.SECONDARY)
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
