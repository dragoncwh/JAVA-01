package cwh.week4.methods;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

public class Method7 implements Runnable {

  public AtomicStampedReference<String> ref;
  private int stamp;

  public Method7(String initVal, int initStamp) {
    this.stamp = initStamp;
    ref = new AtomicStampedReference<>(initVal, initStamp);
  }


  @Override
  public void run() {
    ref.set("Method 7 Result", stamp + 1);
  }
}
