package cwh.week5.beanconfig.beans;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

@Component
public class StudentFactory implements FactoryBean<Student> {

  private static List<Character> nameChars =
      ImmutableList.copyOf(
          "abcdefghijklmnopqrstuvwxyz".chars().mapToObj(i -> (char)i).toArray(Character[]::new)
      );

  @Override
  public Student getObject() throws Exception {
    Random random = new Random();
    int nameLength = 5 + random.nextInt(3);
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < nameLength; i++) {
      sb.append(nameChars.get(random.nextInt(nameChars.size())));
    }
    return new Student(Student.getNextId(), sb.toString());
  }

  @Override
  public Class<?> getObjectType() {
    return Student.class;
  }

  @Override
  public boolean isSingleton() {
    return false;
  }
}
