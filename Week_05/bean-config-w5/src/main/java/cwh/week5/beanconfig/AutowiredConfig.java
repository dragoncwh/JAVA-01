package cwh.week5.beanconfig;

import cwh.week5.beanconfig.beans.Klass;
import cwh.week5.beanconfig.beans.Student;
import cwh.week5.beanconfig.beans.StudentFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE, classes = {AnnotationConfig.class}))
public class AutowiredConfig {

  // @Bean(name = "studeng")
  // public StudentFactory studentFactory() {
  //   return new StudentFactory();
  // }

  @Autowired
  private StudentFactory studentFactory;

  @Bean
  @Qualifier("school")
  public Student student() throws Exception {
    return studentFactory.getObject();
  }

  @Bean
  public List<Student> schoolKlassStudents() throws Exception {
    List<Student> students = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      students.add(studentFactory.getObject());
    }
    return Collections.unmodifiableList(students);
  }

  @Autowired
  @Bean
  @Qualifier("school")
  public Klass schoolKlass(List<Student> schoolKlassStudents) {
    return new Klass(schoolKlassStudents);
  }


  public static void main(String[] args) {
    ApplicationContext context =
        new AnnotationConfigApplicationContext(AutowiredConfig.class);
    System.out.println(context.getBean("school"));
  }
}
