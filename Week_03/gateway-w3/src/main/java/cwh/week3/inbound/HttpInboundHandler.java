package cwh.week3.inbound;

import cwh.week3.filter.HeaderHttpRequestFilter;
import cwh.week3.filter.HttpRequestFilter;
import cwh.week3.outbound.HttpOutboundHandler;
import cwh.week3.outbound.netty.NettyHttpClientOutboundHandler;
import cwh.week3.outbound.okhttp.OkhttpOutboundHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.ReferenceCountUtil;
import java.io.Closeable;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpInboundHandler extends ChannelInboundHandlerAdapter {

  private static Logger logger = LoggerFactory.getLogger(HttpInboundHandler.class);
  private final List<String> proxyServer;
  private HttpOutboundHandler handler;
  private HttpRequestFilter filter = new HeaderHttpRequestFilter();

  public HttpInboundHandler(List<String> proxyServer) {
    this.proxyServer = proxyServer;

    this.handler = new OkhttpOutboundHandler(proxyServer);
    // this.handler = new NettyHttpClientOutboundHandler(proxyServer);
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
    ctx.flush();
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    try {
      FullHttpRequest fullRequest = (FullHttpRequest) msg;
      System.out.println("收到Client请求: " + ctx.channel().remoteAddress());
      handler.handle(fullRequest, ctx, filter);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      ReferenceCountUtil.release(msg);
    }
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) {
    if (handler instanceof Closeable) {
      try {
        ((Closeable) handler).close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
