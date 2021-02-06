package cwh.week4.methods;

public class Method1 extends Thread {
  public String result;

  public Method1(String threadName) {
    super(threadName);
  }

  @Override
  public void run() {
    result = "method 1 result";
  }
}
