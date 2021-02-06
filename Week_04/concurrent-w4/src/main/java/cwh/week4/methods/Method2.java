package cwh.week4.methods;

public class Method2 extends Thread {
  public volatile String result;

  public Method2(String threadName) {
    super(threadName);
  }

  @Override
  public void run() {
    result = "method 2 result";
  }
}
