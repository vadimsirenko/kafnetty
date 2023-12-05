package org.kafnetty.netty.handler.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kafnetty.dto.BaseDto;
import org.kafnetty.dto.UserDto;
import org.kafnetty.netty.handler.auth.AuthProcessor;
import org.kafnetty.netty.handler.auth.JwtService;
import org.kafnetty.netty.handler.auth.Session;
import org.kafnetty.netty.handler.websocket.BaseWebSocketServerHandler;
import org.kafnetty.service.AuthenticationService;
import org.kafnetty.service.ChatService;
import org.kafnetty.type.MessageType;
import org.kafnetty.type.OperationType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static org.kafnetty.config.ServerConstants.*;
import static org.kafnetty.netty.handler.auth.UserContext.hasContext;
import static org.kafnetty.netty.handler.auth.UserContext.setContext;

@Component
@ChannelHandler.Sharable
@Qualifier("httpServerHandler")
@RequiredArgsConstructor
@Slf4j
public class HttpServerHandler extends BaseWebSocketServerHandler<FullHttpRequest> {
    protected static final AttributeKey<WebSocketServerHandshaker> HANDSHAKER_ATTR_KEY = AttributeKey.valueOf(WebSocketServerHandshaker.class, "HANDSHAKER");
    public static final AttributeKey<Session> SESSION_ATTR_KEY = AttributeKey.valueOf("SESSION");

    private final ChatService chatService;
    private final HttpProcessor httpProcessor;
    private final AuthProcessor authProcessor;
    private static String getWebSocketLocation(HttpRequest req) {
        String location = req.headers().get(HOST) + WEBSOCKET_PATH;
        return "ws://" + location;
    }
    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest httpRequest) {
        if (processHttpRequest(ctx, httpRequest)) {
            // Handshake
            WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(httpRequest), null, true);
            WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(httpRequest);
            if (handshaker == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            } else {
                ChannelFuture channelFuture = handshaker.handshake(ctx.channel(), httpRequest);
                // After the handshake is successful, the business logic
                if (channelFuture.isSuccess()) {
                    chatService.InitChannel(ctx.channel());
                    setHandshaker(ctx.channel(), handshaker);
                }
            }
        }
    }
    public boolean processHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        try {
            // Handle a bad request.
            if (!request.decoderResult().isSuccess()) {
                httpProcessor.sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
                return false;
            }
            // GET methods.
            else if (request.method() == GET){
                if (request.method() == GET && request.uri().startsWith(WEBSOCKET_PATH)) {
                    authProcessor.loadContext(ctx, request);
                    return true;
                }
                else if ("/favicon.ico".equals(request.uri()) || "/".equals(request.uri())) {
                    System.out.println("not fount! : " + request.uri());
                    httpProcessor.sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
                }
                else if (request.uri().startsWith(STATIC_CONTENT_PATH) || request.uri().startsWith(CHAT_PATH)) {
                    httpProcessor.sendContentResponse(ctx, request, request.uri().substring(1));
                }
            }
            else if (request.method() == POST && request.uri().equals(LOGON_PATH)) {
               httpProcessor.sendHttpJsonResponse(ctx, request, OK, authProcessor.createUserToken(request));
            } else{
                httpProcessor.sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
            }
            return false;
        } catch (Exception ex) {
            httpProcessor.sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
            log.error("error at process Http request", ex);
            return false;
        }
    }
    public static WebSocketServerHandshaker getHandshaker(Channel channel) {
        return channel.attr(HANDSHAKER_ATTR_KEY).get();
    }
    private static void setHandshaker(Channel channel, WebSocketServerHandshaker handshaker) {
        channel.attr(HANDSHAKER_ATTR_KEY).set(handshaker);
    }
}
