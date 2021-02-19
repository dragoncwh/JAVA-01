package cwh.week5.beanconfig.beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class School {

  @Autowired(required = true)
  @Qualifier("school")
  Klass class1;

  @Autowired
  @Qualifier("school")
  Student student100;

  @Override
  public String toString() {
    return "{Klass: " + class1.toString() + "  " + "Student: " + student100.toString() + "}";
  }
}
