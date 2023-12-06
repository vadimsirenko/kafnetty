package org.kafnetty.netty.handler.http;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kafnetty.netty.config.ServerConstants;
import org.kafnetty.netty.handler.BaseWebSocketServerHandler;
import org.kafnetty.session.UserContext;
import org.kafnetty.service.chat.ChatService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static io.netty.handler.codec.http.HttpHeaderNames.HOST;

@Component
@ChannelHandler.Sharable
@Qualifier("httpServerHandler")
@RequiredArgsConstructor
@Slf4j
public class HttpServerHandler extends BaseWebSocketServerHandler<HttpRequest> {
    private final ChatService chatService;

    private static String getWebSocketLocation(HttpRequest req) {
        String location = req.headers().get(HOST) + ServerConstants.WEBSOCKET_PATH;
        return "ws://" + location;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpRequest httpRequest) {
        if (UserContext.hasContext(ctx.channel())) {
            // Handshake
            WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(httpRequest), null, true);
            WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(httpRequest);
            if (handshaker == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            } else {
                ChannelFuture channelFuture = handshaker.handshake(ctx.channel(), httpRequest);
                // After the handshake is successful, the business logic
                if (channelFuture.isSuccess()) {
                    UserContext.setHandshaker(ctx.channel(), handshaker);
                    chatService.InitChannel(ctx.channel());
                }
            }
        }
    }
}
