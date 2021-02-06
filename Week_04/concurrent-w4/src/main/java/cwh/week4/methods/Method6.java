package cwh.week4.methods;

import java.util.concurrent.CountDownLatch;

public class Method6 {
  public static class CountDownLatchRunnable implements Runnable {
    private CountDownLatch latch;
    public String result;

    public CountDownLatchRunnable(CountDownLatch latch) {
      this.latch = latch;
    }

    @Override
    public void run() {
      result = "Method 6 result";
      latch.countDown();
    }
  }
}
