package cwh.week4.methods;

public class Method10 implements Runnable {
  private Object lock = new Object();

  private String result;

  @Override
  public void run() {
    synchronized (lock) {
      result = "Method 10 Result";
      lock.notifyAll();
    }
  }

  public String getResult() {
    String ret = null;
    synchronized (lock) {
      try {
        if (result == null) {
          lock.wait();
        }
        ret = result;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return ret;
  }
}
