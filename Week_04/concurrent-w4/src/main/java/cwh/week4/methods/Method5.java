package cwh.week4.methods;

import java.util.concurrent.BlockingDeque;

public class Method5 {
  public static class Producer extends Thread {
    private final BlockingDeque<String> deque;

    public Producer(BlockingDeque deque) {
      this.deque = deque;
    }

    @Override
    public void run() {
      deque.offer("Method 5 Result");
    }
  }
}
