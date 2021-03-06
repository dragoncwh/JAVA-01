package cwh.java.geektime.datasourcesplit.student.service;

import cwh.java.geektime.datasourcesplit.student.domain.Student;
import java.util.Collection;

public interface UserService {


  boolean insert(Student student) throws Exception;

  Collection<Student> getAll() throws Exception;
}
