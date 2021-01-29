package cwh.week3.inbound;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.util.List;

public class HttpInboundInitializer extends ChannelInitializer<SocketChannel> {

  private List<String> proxyServer;

  public HttpInboundInitializer(List<String> proxyServer) {
    this.proxyServer = proxyServer;
  }

  @Override
  protected void initChannel(SocketChannel ch) throws Exception {
    ChannelPipeline p = ch.pipeline();

    // p.addLast(new LoggingHandler(LogLevel.INFO));
    p.addLast(new HttpServerCodec());
    p.addLast(new HttpObjectAggregator(1024 * 1024));
    p.addLast(new HttpInboundHandler(this.proxyServer));
  }
}
