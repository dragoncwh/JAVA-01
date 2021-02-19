package cwh.week5.beanconfig.beans;

import java.util.concurrent.atomic.AtomicInteger;

public class Student {
  private static AtomicInteger atomicId = new AtomicInteger(1);

  static int getNextId() {
    return atomicId.getAndAdd(1);
  }

  private int id;
  private String name;

  public Student(String name) {
    this.id = getNextId();
    this.name = name;
  }

  public Student(int id, String name) {
    this.id = id;
    this.name = name;
  }

  @Override
  public String toString() {
    return "{" + "id: " + id + ", " + "name: " + name + "}";
  }
}
