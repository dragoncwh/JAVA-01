package cwh.java.geektime.datasourcesplitshardingjdbc.student.repository;

import cwh.java.geektime.datasourcesplitshardingjdbc.student.domain.Student;
import java.util.Collection;

public interface StudentRepository {

  boolean insert(Student student) throws Exception;

  Collection<Student> getAll() throws Exception;
}
