package cwh.week5.db;

import java.util.concurrent.atomic.AtomicInteger;

public class Student {

  // private static AtomicInteger atomicId = new AtomicInteger(1);
  //
  // private static int getNextId() {
  //   return atomicId.getAndAdd(1);
  // }

  private int id;
  private String name;

  public Student(String name) {
    this.name = name;
  }

  public Student(int id, String name) {
    this.id = id;
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return String.format(
        "Student[id=%d, name='%s']", id, name);
  }
}
