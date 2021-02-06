package cwh.week4;

import cwh.week4.methods.Method1;
import cwh.week4.methods.Method10;
import cwh.week4.methods.Method2;
import cwh.week4.methods.Method5;
import cwh.week4.methods.Method5.Producer;
import cwh.week4.methods.Method6.CountDownLatchRunnable;
import cwh.week4.methods.Method7;
import cwh.week4.methods.Method8;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class Homework {

  public static void main(String[] args) throws Exception {

    long start=System.currentTimeMillis();

    String result = method1();
    // String result = method2();
    // String result = method3();
    // String result = method4();
    // String result = method5();
    // String result = method6();
    // String result = method7();
    // String result = method8();
    // String result = method9();
    // String result = method10();

    System.out.println("异步计算结果为："+result);

    System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");

  }

  // synchronized
  private static String method10() throws Exception {
    Method10 method10 = new Method10();
    CompletableFuture<Void> future =
        CompletableFuture.runAsync(method10);
    return method10.getResult();
  }

  // FutureTask
  private static String method9() throws Exception {
    FutureTask<String> futureTask = new FutureTask<>(() -> "Method 9 Result");
    new Thread(futureTask).start();
    return futureTask.get();
  }

  // Lock + Condition
  private static String method8() throws Exception {
    Method8 method8 = new Method8();
    CompletableFuture<Void> future =
        CompletableFuture.runAsync(method8);
    return method8.getResult();
  }

  // AtomicStampedReference
  private static String method7() throws Exception {
    String initVal = "";
    int initStamp = 0;
    Method7 method7 = new Method7(initVal, initStamp);
    CompletableFuture<Void> future =
        CompletableFuture.runAsync(method7);
    while (method7.ref.getStamp() == initStamp) {
      TimeUnit.MILLISECONDS.sleep(100);
    }
    return method7.ref.getReference();
  }

  // CountDownLatch
  private static String method6() throws Exception {
    CountDownLatch latch = new CountDownLatch(1);
    CountDownLatchRunnable runnable = new CountDownLatchRunnable(latch);
    CompletableFuture<Void> future =
        CompletableFuture.runAsync(runnable);
    latch.await();
    return runnable.result;
  }

  // producer + BlockingDeque
  private static String method5() throws Exception {
    BlockingDeque<String> deque = new LinkedBlockingDeque<>(1);
    Producer producer = new Method5.Producer(deque);
    producer.start();
    return deque.take();
  }

  // ExecutorService + callable
  private static String method4() throws Exception {
    Callable<String> callable = () -> "Method 4 result";
    ExecutorService es = Executors.newFixedThreadPool(1);
    Future<String> future = es.submit(callable);
    String result = future.get();
    es.shutdown();
    return result;
  }

  // CompletableFuture + callable
  private static String method3() throws Exception {
    Supplier<String> callable = () -> "Method 3 result";
    CompletableFuture<String> future = CompletableFuture.supplyAsync(callable);
    return future.get();
  }

  // while loop + volatile
  private static String method2() throws Exception {
    Method2 method2 = new Method2("method 2 thread");
    method2.start();
    while (method2.result == null) {
      TimeUnit.MILLISECONDS.sleep(100);
    }
    return method2.result;
  }

  // simple thread
  private static String method1() throws Exception {
    Method1 method1 = new Method1("method 1 thread");
    method1.start();
    method1.join();
    return method1.result;
  }
}