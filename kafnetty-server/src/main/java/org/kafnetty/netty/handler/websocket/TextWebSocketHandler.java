package org.kafnetty.netty.handler.websocket;

import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kafnetty.dto.BaseDto;
import org.kafnetty.dto.ErrorDto;
import org.kafnetty.netty.handler.http.HttpServerHandler;
import org.kafnetty.service.ChatService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
@Qualifier("webSocketServerHandler")
@RequiredArgsConstructor
@Slf4j
public class TextWebSocketHandler extends BaseWebSocketServerHandler<TextWebSocketFrame> {
    private final ChatService chatService;
    @Override
    public void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
        log.info("user: {}", HttpServerHandler.getClientFromContext(ctx.channel()));
        try {
            if (!chatService.existsChannelUser(ctx.channel())) {
                ErrorDto.createCommonError("You can't chat without logging in").writeAndFlush(ctx.channel());
            } else {
                chatService.processMessage(BaseDto.decode(frame.text()), ctx.channel());
            }
        } catch (Exception e) {
            log.error("error at process WebSocket request", e);
        }
    }
}
