package cwh.week3.httpservers;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class HttpServer03 {

  public static void main(String[] args) throws IOException {
    ExecutorService executorService = Executors.newFixedThreadPool(
        Runtime.getRuntime().availableProcessors() + 2);
    final ServerSocket serverSocket = new ServerSocket(8803);
    while (true) {
      try {
        final Socket socket = serverSocket.accept();
        executorService.execute(() -> service(socket));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  // public static AtomicLong atomicLong = new AtomicLong(0);

  private static void service(Socket socket) {
    try {

      // Thread.sleep(5);
      // System.out.println(atomicLong.addAndGet(1));
      PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), false);
      printWriter.println("HTTP/1.1 200 OK");
      printWriter.println("Content-Type:text/html;charset=utf-8");
      String body = "hello,nio3";
      printWriter.println("Content-Length:" + body.getBytes().length);
      printWriter.println();
      printWriter.write(body);
      // printWriter.flush();
      printWriter.close();
      socket.close();
    } catch (IOException e) { // | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
