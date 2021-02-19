package cwh.week5.starter;

import cwh.week5.starter.beans.ISchool;
import cwh.week5.starter.beans.Klass;
import cwh.week5.starter.beans.School;
import cwh.week5.starter.beans.Student;
import java.util.Arrays;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "custom.autoconfig", name = "enable")
public class CustomSpringBootConfig {
  @Bean("student100")
  public Student getStudent100() {
    Student student = new Student();
    student.setId(100);
    student.setName("cwh");
    return student;
  }

  @Bean
  public Klass getKlass() {
    Klass klass = new Klass();
    klass.setStudents(Arrays.asList(
        new Student("student1"),
        new Student("student2")));
    return klass;
  }

  @Bean
  public ISchool getSchool() {
    return new School();
  }

  // public static void main(String[] args) {
  //   SpringApplication.run(CustomSpringBootConfig.class);
  // }
}
