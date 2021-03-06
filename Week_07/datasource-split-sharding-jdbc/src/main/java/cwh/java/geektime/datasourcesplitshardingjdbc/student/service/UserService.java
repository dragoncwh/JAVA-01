package cwh.java.geektime.datasourcesplitshardingjdbc.student.service;

import cwh.java.geektime.datasourcesplitshardingjdbc.student.domain.Student;
import java.util.Collection;

public interface UserService {


  boolean insert(Student student) throws Exception;

  Collection<Student> getAll() throws Exception;
}
