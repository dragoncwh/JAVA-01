package cwh.java.geektime.datasourcesplit.student.repository;

import cwh.java.geektime.datasourcesplit.student.domain.Student;
import java.util.Collection;

public interface StudentRepository {

  boolean insert(Student student) throws Exception;

  Collection<Student> getAll() throws Exception;
}
