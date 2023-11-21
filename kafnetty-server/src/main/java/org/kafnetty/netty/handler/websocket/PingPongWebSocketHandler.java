package org.kafnetty.netty.handler.websocket;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@ChannelHandler.Sharable
@Component
public class PingPongWebSocketHandler extends BaseWebSocketServerHandler<PingWebSocketFrame> {
    @Override
    public void channelRead0(ChannelHandlerContext ctx, PingWebSocketFrame frame) {
        ctx.write(new PongWebSocketFrame(frame.content().retain()));
    }
}