package cwh.week3.outbound.netty;

import cwh.week3.filter.HeaderHttpResponseFilter;
import cwh.week3.filter.HttpRequestFilter;
import cwh.week3.filter.HttpResponseFilter;
import cwh.week3.outbound.HttpOutboundHandler;
import cwh.week3.outbound.Utils;
import cwh.week3.router.HttpEndpointRouter;
import cwh.week3.router.RandomHttpEndpointRouter;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

public class NettyHttpClientOutboundHandler implements HttpOutboundHandler {

  private List<String> backendUrls;

  private ChannelGroup channels;

  private HttpEndpointRouter router = new RandomHttpEndpointRouter();

  public NettyHttpClientOutboundHandler(List<String> backends) {
    this.backendUrls = backends.stream().map(Utils::formatUrl).collect(Collectors.toList());
  }

  @Override
  public void handle(FullHttpRequest fullRequest, ChannelHandlerContext ctx,
      HttpRequestFilter filter) {
    final String backendUrl = router.route(this.backendUrls);
    filter.filter(fullRequest, ctx);
    try {
      URI uri = new URI(backendUrl + fullRequest.uri());
      handle(ctx, fullRequest, uri);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  static void closeOnFlush(Channel ch) {
    if (ch.isActive()) {
      ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
  }

  private void handle(ChannelHandlerContext ctx,
      FullHttpRequest originalRequest, URI uri) throws Exception {

    if (!originalRequest.method().equals(HttpMethod.GET)) {
      return;
    }

    EventLoopGroup workerGroup = new NioEventLoopGroup();
    try {
      Bootstrap b = new Bootstrap()
          .group(workerGroup)
          .channel(NioSocketChannel.class)
          .option(ChannelOption.SO_KEEPALIVE, true)
          .handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
              ch.pipeline().addLast(
                  new HttpClientCodec(),
                  new HttpObjectAggregator(1024 * 1024),
                  new NettyTargetHandler(ctx.channel()));
            }
          });

      ChannelFuture cf = b.connect(uri.getHost(), uri.getPort()).sync();

      byte[] bodyBytes = new byte[originalRequest.content().readableBytes()];
      originalRequest.content().readBytes(bodyBytes);
      HttpRequest request = new DefaultFullHttpRequest(
          HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getRawPath(),
          Unpooled.wrappedBuffer(bodyBytes));

      request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
      String maoHeader = "mao";
      if (originalRequest.headers().contains(maoHeader)) {
        request.headers().set(maoHeader, originalRequest.headers().get(maoHeader));
      }

      Channel ch = cf.channel();
      System.out.println("转发请求至: " + uri.toString());
      ch.writeAndFlush(request);
      ch.closeFuture().sync();
    } finally {
      workerGroup.shutdownGracefully();
    }
  }
}