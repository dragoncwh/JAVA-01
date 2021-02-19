package cwh.week5.starter;

import cwh.week5.starter.beans.ISchool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class StarterApplication {

  @Autowired
  private ISchool school;

  public static void main(String[] args) {
    ApplicationContext context = SpringApplication.run(StarterApplication.class);
    ISchool iSchool = context.getBean(ISchool.class);
    iSchool.ding();
  }
}
