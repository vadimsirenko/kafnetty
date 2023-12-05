package org.kafnetty.netty.handler.websocket;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kafnetty.netty.handler.http.HttpServerHandler;
import org.kafnetty.service.ChatService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
@RequiredArgsConstructor
@Slf4j
public class CloseWebSocketHandler extends BaseWebSocketServerHandler<CloseWebSocketFrame> {
    @Override
    public void channelRead0(ChannelHandlerContext ctx, CloseWebSocketFrame frame) {
        WebSocketServerHandshaker handshaker = HttpServerHandler.getHandshaker(ctx.channel());
        handshaker.close(ctx.channel(), frame.retain());
        System.out.println(ctx.channel() + " closed");
    }
}