package cwh.week5.starter.beans;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class Student implements Serializable {

  private static AtomicInteger atomicId = new AtomicInteger(1);

  private static int getNextId() {
    return atomicId.getAndAdd(1);
  }

  private int id;
  private String name;

  public Student() {
  }

  public Student(String name) {
    this.id = getNextId();
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

  public void init(){
    System.out.println("hello...........");
  }

  // public Student create(){
  //   return new Student(101,"KK101");
  // }

  @Override
  public String toString() {
    return "{" + "id: " + id + ", " + "name: " + name + "}";
  }
}

