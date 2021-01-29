package cwh.week3.outbound.okhttp;

import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import cwh.week3.filter.HeaderHttpResponseFilter;
import cwh.week3.filter.HttpRequestFilter;
import cwh.week3.filter.HttpResponseFilter;
import cwh.week3.outbound.HttpOutboundHandler;
import cwh.week3.outbound.NamedThreadFactory;
import cwh.week3.outbound.Utils;
import cwh.week3.router.HttpEndpointRouter;
import cwh.week3.router.RandomHttpEndpointRouter;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

public class OkhttpOutboundHandler implements HttpOutboundHandler {
  private static OkHttpClient client = initClient();
  private List<String> backendUrls;
  private ExecutorService proxyService;

  private HttpResponseFilter filter = new HeaderHttpResponseFilter();
  private HttpEndpointRouter router = new RandomHttpEndpointRouter();

  public OkhttpOutboundHandler(List<String> backends) {
    this.backendUrls = backends.stream().map(Utils::formatUrl).collect(Collectors.toList());

    int cores = Runtime.getRuntime().availableProcessors();
    long keepAliveTime = 1000;
    int queueSize = 2048;
    RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();//.DiscardPolicy();
    proxyService = new ThreadPoolExecutor(cores, cores,
        keepAliveTime, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(queueSize),
        new NamedThreadFactory("proxyService"), handler);
  }

  private static OkHttpClient initClient() {
    return new OkHttpClient().newBuilder()
        .connectTimeout(10000, TimeUnit.MILLISECONDS)
        .readTimeout(10000, TimeUnit.MILLISECONDS)
        // .retryOnConnectionFailure(true)
        .build();
  }

  // public static AtomicLong atomicLong = new AtomicLong();

  @Override
  public void handle(FullHttpRequest fullRequest, ChannelHandlerContext ctx,
      HttpRequestFilter filter) {
    if (!fullRequest.method().equals(HttpMethod.GET)) {
      return;
    }

    // System.out.println(atomicLong.addAndGet(1));
    String backendUrl = router.route(this.backendUrls);
    // System.out.println(fullRequest.uri());
    final String url = backendUrl + fullRequest.uri();
    filter.filter(fullRequest, ctx);
    proxyService.submit(()->fetchGet(fullRequest, ctx, url));
  }

  private void fetchGet(final FullHttpRequest inbound,
      final ChannelHandlerContext ctx, final String url) {
    System.out.println("转发请求至: " + url);
    Request.Builder builder = new Builder();
    builder = builder.url(url);
    // builder = builder.header("connection", "keep-alive");
    builder = builder.header("connection", "close");
    String maoHeader = "mao";
    if (inbound.headers().contains(maoHeader)) {
      builder = builder.header(maoHeader, inbound.headers().get(maoHeader));
    }
    Request request = builder.build();

    Call call = client.newCall(request);
    call.enqueue(new Callback() {
      @Override
      public void onFailure(@NotNull Call call, @NotNull IOException e) {
        e.printStackTrace();
      }

      @Override
      public void onResponse(@NotNull Call call, @NotNull Response response) {
        try {
          if (!response.isSuccessful()) {
            System.out.println("收到后端Server错误: " + response.code());
            throw new IOException("Unexpected code " + response);
          }
          handleResponse(inbound, ctx, response);
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          response.close();
        }
      }
    });
  }

  private void handleResponse(final FullHttpRequest fullRequest,
      final ChannelHandlerContext ctx, final Response endpointResponse) throws Exception {
    FullHttpResponse response = null;
    try {
      String bodyStr = endpointResponse.body().string();
      byte[] body = bodyStr.getBytes(StandardCharsets.UTF_8);

      System.out.println("收到后端Server响应: " + endpointResponse.request().url()
          + " : " + new String(body, 0, body.length, StandardCharsets.UTF_8));

      response = new DefaultFullHttpResponse(HTTP_1_1,
          HttpResponseStatus.valueOf(endpointResponse.code()), Unpooled.wrappedBuffer(body));

      // Headers headers = endpointResponse.headers();
      // Iterator<Pair<String, String>> it = headers.iterator();
      // while (it.hasNext()) {
      //   Pair<String, String> pair = it.next();
      //   response.headers().set(pair.component1(), pair.component2());
      // }

      response.headers().set("Content-Type", "application/json");
      response.headers().setInt("Content-Length", body.length);
      filter.filter(response);
    } catch (Exception e) {
      e.printStackTrace();
      response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
      // exceptionCaught(ctx, e);
    } finally {
      if (fullRequest != null) {
        if (!HttpUtil.isKeepAlive(fullRequest)) {
          ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        } else {
          //response.headers().set(CONNECTION, KEEP_ALIVE);
          ctx.write(response);
        }
      }
      ctx.flush();
    }
  }

  // private void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
  //   cause.printStackTrace();
  //   ctx.close();
  // }
}
