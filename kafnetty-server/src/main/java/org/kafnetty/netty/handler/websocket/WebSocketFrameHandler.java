package org.kafnetty.netty.handler.websocket;

import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kafnetty.dto.BaseDto;
import org.kafnetty.dto.ErrorDto;
import org.kafnetty.netty.handler.BaseWebSocketServerHandler;
import org.kafnetty.netty.handler.auth.UserContext;
import org.kafnetty.service.ChatService;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
@RequiredArgsConstructor
@Slf4j
public class WebSocketFrameHandler extends BaseWebSocketServerHandler<WebSocketFrame> {
    private final ChatService chatService;
    @Override
    public void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof CloseWebSocketFrame && UserContext.hasContext(ctx.channel())
                && UserContext.getContext(ctx.channel()).getHandshaker() != null) {
            beforeCloseChannel(ctx.channel());
            UserContext.getContext(ctx.channel()).getHandshaker().close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            System.out.println(ctx.channel() + " closed");
        } else if (frame instanceof PingWebSocketFrame) // binary date
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
        else if (!(frame instanceof TextWebSocketFrame)) // text data
            throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
        else
            processWebSocketRequest(ctx, (TextWebSocketFrame)frame);
    }

    private void processWebSocketRequest(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
        try {
            if (!UserContext.hasContext(ctx.channel())) {
                ErrorDto.createCommonError("You can't chat without logging in").writeAndFlush(ctx.channel());
            } else {
                chatService.processMessage(BaseDto.decode(frame.text()), ctx.channel());
            }
        } catch (Exception e) {
            log.error("error at process WebSocket request", e);
        }
    }

    public void beforeCloseChannel(Channel channel){
        chatService.removeChannel(channel);
    }
}
