package cwh.week2;

import static org.junit.Assert.assertEquals;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Test;

public class MyOkHttpClientTest {
  @Test
  public void testSimpleTest() throws Exception {
    MockWebServer server = new MockWebServer();

    server.start();

    String response = "Hello world";
    MockResponse mockResponse = new MockResponse();
    mockResponse.setResponseCode(200);
    mockResponse.setBody(response);
    server.enqueue(mockResponse);

    HttpUrl url = server.url("/");
    assertEquals(response, MyOkHttpClient.simpleGet(url.toString()));

    RecordedRequest recordedRequest = server.takeRequest();
    assertEquals("GET", recordedRequest.getMethod());

    server.shutdown();
  }
}
