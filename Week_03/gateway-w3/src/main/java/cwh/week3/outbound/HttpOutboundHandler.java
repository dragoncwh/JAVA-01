package cwh.week3.outbound;

import cwh.week3.filter.HttpRequestFilter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public interface HttpOutboundHandler {
  void handle(final FullHttpRequest fullRequest,
      final ChannelHandlerContext ctx, HttpRequestFilter filter);
}
