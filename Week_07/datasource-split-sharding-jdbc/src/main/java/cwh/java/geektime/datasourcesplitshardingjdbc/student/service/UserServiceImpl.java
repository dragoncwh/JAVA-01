package cwh.java.geektime.datasourcesplitshardingjdbc.student.service;

import cwh.java.geektime.datasourcesplitshardingjdbc.student.domain.Student;
import cwh.java.geektime.datasourcesplitshardingjdbc.student.repository.StudentRepository;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  StudentRepository studentRepository;

  @Override
  public boolean insert(Student student) throws Exception {
    return studentRepository.insert(student);
  }

  @Override
  public Collection<Student> getAll() throws Exception {
    return studentRepository.getAll();
  }
}
