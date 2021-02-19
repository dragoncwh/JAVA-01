package cwh.week5.beanconfig;

import cwh.week5.beanconfig.beans.Klass;
import cwh.week5.beanconfig.beans.School;
import cwh.week5.beanconfig.beans.Student;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE, classes = {AutowiredConfig.class, School.class, Klass.class}))
public class AnnotationConfig {

  @Bean
  public Student singletonStudent() {
    return new Student("Jenny");
  }

  public static void main(String[] args) {
    ApplicationContext context =
        new AnnotationConfigApplicationContext(AnnotationConfig.class);
    String beanName = "singletonStudent";
    System.out.println("Is " + beanName + " in ApplicationContext: " +
        context.containsBean(beanName));
    System.out.println(context.getBean(beanName, Student.class));
  }
}
