package cwh.week3.outbound.netty;

import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import cwh.week3.filter.HeaderHttpResponseFilter;
import cwh.week3.filter.HttpResponseFilter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import java.nio.charset.StandardCharsets;

public class NettyTargetHandler extends ChannelInboundHandlerAdapter {

  private final HttpResponseFilter filter = new HeaderHttpResponseFilter();
  private final Channel inboundChannel;

  public NettyTargetHandler(Channel inboundChannel) {
    this.inboundChannel = inboundChannel;
  }

  @Override
  public void channelRead(final ChannelHandlerContext ctx, Object msg) {
    FullHttpResponse response = null;
    try {
      // System.out.println("xxx");
      if (msg instanceof FullHttpResponse) {
        FullHttpResponse endpointResponse = (FullHttpResponse) msg;

        ByteBuf buf = endpointResponse.content();
        byte[] body = new byte[buf.readableBytes()];
        buf.getBytes(buf.readerIndex(), body);
        System.out.println("收到后端Server响应: " + ctx.channel().remoteAddress()
            + " : " + new String(body, 0, body.length, StandardCharsets.UTF_8));
        response = new DefaultFullHttpResponse(
            HTTP_1_1, endpointResponse.status(), Unpooled.wrappedBuffer(body));
        response.headers().set("Content-Type", "application/json");
        response.headers().setInt("Content-Length", body.length);

        filter.filter(response);
      }

    } catch (Exception e) {
      e.printStackTrace();
      response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
      // exceptionCaught(ctx, e);
    } finally {
      if (response != null) {
        if (!HttpUtil.isKeepAlive(response)) {
          inboundChannel.write(response).addListener(ChannelFutureListener.CLOSE);
        } else {
          //response.headers().set(CONNECTION, KEEP_ALIVE);
          inboundChannel.write(response);
        }

        inboundChannel.flush();
      }
    }
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) {
    NettyHttpClientOutboundHandler.closeOnFlush(inboundChannel);
  }
}
