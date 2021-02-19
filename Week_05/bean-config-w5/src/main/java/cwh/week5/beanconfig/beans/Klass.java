package cwh.week5.beanconfig.beans;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class Klass {
  List<Student> students;

  public Klass() {}

  public Klass(List<Student> students) {
    this.students = students;
  }

  public List<Student> getStudents() {
    return students;
  }

  public void setStudents(List<Student> students) {
    this.students = students;
  }

  @Override
  public String toString() {
    return students.toString();
  }
}
