package cwh.week3.httpservers;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicLong;

public class HttpServer01 {

  public static void main(String[] args) throws IOException {
    ServerSocket serverSocket = new ServerSocket(8801);
    while (true) {
      try {
        Socket socket = serverSocket.accept();
        service(socket);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  // public static AtomicLong atomicLong = new AtomicLong(0);

  private static void service(Socket socket) {
    try {
      // System.out.println(atomicLong.addAndGet(1));
      PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), false);
      printWriter.println("HTTP/1.1 200 OK");
      printWriter.println("Content-Type:text/html;charset=utf-8");
      String body = "hello,nio1";
      printWriter.println("Content-Length:" + body.getBytes().length);
      printWriter.println();
      printWriter.write(body);
      // printWriter.flush();
      printWriter.close();
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
