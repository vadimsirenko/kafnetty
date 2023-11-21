package org.kafnetty.netty.handler.websocket;

import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kafnetty.dto.ErrorDto;
import org.kafnetty.service.ChatService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static org.kafnetty.service.ChatServiceImpl.CLIENT_ATTRIBUTE_KEY;

@Component
@ChannelHandler.Sharable
@Qualifier("webSocketServerHandler")
@RequiredArgsConstructor
@Slf4j
public class TextWebSocketHandler extends BaseWebSocketServerHandler<TextWebSocketFrame> {
    private final ChatService chatService;
    @Override
    public void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
        log.info("user: {}", ctx.channel().attr(CLIENT_ATTRIBUTE_KEY).get().getNickName());
        try {
            if (!chatService.existsChannelUser(ctx.channel())) {
                ErrorDto.createCommonError("You can't chat without logging in").writeAndFlush(ctx.channel());
            } else {
                chatService.processMessage(frame.text(), ctx.channel());
            }
        } catch (Exception e) {
            log.error("error at process WebSocket request", e);
        }
    }
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        Channel incoming = ctx.channel();
        System.out.println("Received " + incoming.remoteAddress() + " handshake request");
    }
}
