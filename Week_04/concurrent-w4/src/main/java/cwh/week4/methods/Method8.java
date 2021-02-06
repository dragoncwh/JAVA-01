package cwh.week4.methods;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Method8 implements Runnable {
  private Lock lock = new ReentrantLock();
  private Condition hasResult = lock.newCondition();

  private String result = null;

  @Override
  public void run() {
    try {
      lock.lock();
      result = "Method 8 result";
      hasResult.signalAll();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      lock.unlock();
    }
  }

  public String getResult() {
    String ret = null;
    try {
      lock.lock();
      if (result == null) {
        hasResult.await();
      }
      ret = result;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      lock.unlock();
    }
    return ret;
  }
}
