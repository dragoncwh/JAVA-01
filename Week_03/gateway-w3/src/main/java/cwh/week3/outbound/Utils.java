package cwh.week3.outbound;

public class Utils {
  public static String formatUrl(String backend) {
    return backend.endsWith("/") ? backend.substring(0, backend.length()-1) : backend;
  }
}
