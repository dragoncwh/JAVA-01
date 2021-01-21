package cwh.week2;

import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyOkHttpClient {

  private static final OkHttpClient client = initClient();

  public OkHttpClient getInstance() {
    return client;
  }

  private static OkHttpClient initClient() {
    return new OkHttpClient();
  }

  public static String simpleGet(String url) throws IOException {
    // HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
    // String finalurl = urlBuilder.build().toString();
    Request request = new Request.Builder()
        .url(url)
        .build();

    try (Response response = client.newCall(request).execute()) {
      return response.body().string();
    }
  }

  public static void main(String[] args) throws IOException {
    String url = "http://localhost:8801";
    if (args != null && args.length == 1) {
      url = args[0];
    }

    MyOkHttpClient.simpleGet(url);
  }
}
