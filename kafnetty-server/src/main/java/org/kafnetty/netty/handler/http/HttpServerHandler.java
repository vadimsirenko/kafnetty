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
import org.kafnetty.dto.ClientDto;
import org.kafnetty.dto.ErrorDto;
import org.kafnetty.netty.handler.websocket.BaseWebSocketServerHandler;
import org.kafnetty.service.ChatService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static org.kafnetty.config.ServerConstants.*;

@Component
@ChannelHandler.Sharable
@Qualifier("httpServerHandler")
@RequiredArgsConstructor
@Slf4j
public class HttpServerHandler extends BaseWebSocketServerHandler<FullHttpRequest> {
    protected static final AttributeKey<WebSocketServerHandshaker> HANDSHAKER_ATTR_KEY = AttributeKey.valueOf(WebSocketServerHandshaker.class, "HANDSHAKER");
    public static final AttributeKey<ClientDto> CLIENT_ATTR_KEY = AttributeKey.valueOf("CLIENT");

    private final ChatService chatService;
    private final HttpProcessor httpProcessor;
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
            if (request.method() == GET){
                if ("/favicon.ico".equals(request.uri()) || "/".equals(request.uri())) {
                    System.out.println("not fount! : " + request.uri());
                    httpProcessor.sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
                    return false;
                }
                if (request.uri().startsWith(STATIC_CONTENT_PATH) || request.uri().startsWith(CHAT_PATH)) {
                    httpProcessor.handleResource(ctx, request, request.uri().substring(1));
                    return false;
                }
                if (!request.uri().startsWith(WEBSOCKET_PATH)) {
                    return false;
                }

                Map<String, List<String>> requestParams = new QueryStringDecoder(request.uri()).parameters();

                if (requestParams.isEmpty() || !requestParams.containsKey(HTTP_PARAM_REQUEST)) {
                    httpProcessor.handleResource(ctx, request, request.uri().substring(1));
                    return false;
                }
                // TODO Реализовать проверку jwt token
                String jsonMessage = new String(Base64.getDecoder().decode(requestParams.get(HTTP_PARAM_REQUEST).get(0)), StandardCharsets.UTF_8);
                chatService.processMessage(BaseDto.decode(jsonMessage), ctx.channel());
                setClientToContext(ctx.channel(), (ClientDto)ClientDto.decode(jsonMessage));
                return true;

            } else if(request.method() == POST && request.uri().equals(LOGON_PATH)){
                ByteBuf jsonBuf = request.content();
                String jsonStr = jsonBuf.toString(CharsetUtil.UTF_8);
                BaseDto baseDto = BaseDto.decode(jsonStr);
                if(baseDto == null){
                    httpProcessor.sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
                    return false;
                }
                // TODO проверка аутентикации и возвращение jwt token
                chatService.processMessage(baseDto, ctx.channel());
                setClientToContext(ctx.channel(), (ClientDto)baseDto);
                httpProcessor.sendHttpJsonResponse(ctx, request, OK, (ClientDto)baseDto);
                return false;
            } else{
                httpProcessor.sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
                return false;
            }
        } catch (RuntimeException ex) {
            httpProcessor.sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
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
    private static void setClientToContext(Channel channel, ClientDto clientDto) {
        channel.attr(CLIENT_ATTR_KEY).set(clientDto);
    }
    public static ClientDto getClientFromContext(Channel channel) {
        return channel.attr(CLIENT_ATTR_KEY).get();
    }
}
