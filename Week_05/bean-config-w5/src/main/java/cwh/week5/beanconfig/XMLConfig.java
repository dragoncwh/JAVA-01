package cwh.week5.beanconfig;

import cwh.week5.beanconfig.beans.Klass;
import cwh.week5.beanconfig.beans.Student;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class XMLConfig {

  public static void main(String[] args) {
    ApplicationContext context =
        new ClassPathXmlApplicationContext("config/beans.xml");

    System.out.println(context.getBean("studentFactory", Student.class));
    System.out.println(context.getBean("studentFactory", Student.class));

    Klass klass = context.getBean("klass", Klass.class);
    System.out.println(klass);
  }
}
