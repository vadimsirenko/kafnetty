package org.kafnetty.service;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kafnetty.dto.channel.ChannelErrorDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@Component
@RequiredArgsConstructor
@Slf4j
public class HttpRequestServiceImpl implements HttpRequestService {
    @Autowired
    private ChatService chatService;
    @Value("${server.web-socket-path}")
    private String webSocketPath;
    @Value("${server.static-path}")
    private String staticPath;
    @Value("${server.chat-path}")
    private String chatPath;
    private final HttpResponseProcessor httpResponseProcessor;

    @Override
    public void processWebSocketRequest(Channel channel, WebSocketFrame frame) {
        try {
            if (!chatService.existsUserProfile(channel)) {
                ChannelErrorDto.createCommonError("You can't chat without logging in").writeAndFlush(channel);
            } else {
                chatService.processMessage(((TextWebSocketFrame) frame).text(), channel);
            }
        } catch (Exception e) {
            log.error("error at process WebSocket request", e);
        }
    }
    @Override
    public boolean processHttpRequest(ChannelHandlerContext ctx, HttpRequest request) {
        try {
            // Handle a bad request.
            if (!request.decoderResult().isSuccess()) {
                httpResponseProcessor.sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
                return false;
            }
            System.out.println(request.uri());
            // Allow only GET methods.
            if (request.method() != GET) {
                httpResponseProcessor.sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
                return false;
            }
            if ("/favicon.ico".equals(request.uri()) || "/".equals(request.uri())) {
                System.out.println("not fount! : " + request.uri());
                httpResponseProcessor.sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
                return false;
            }

            if (request.uri().startsWith(staticPath) || request.uri().startsWith(chatPath)) {
                httpResponseProcessor.handleResource(ctx, request, request.uri().substring(1));
                return false;
            }
            if (!request.uri().startsWith(webSocketPath)) {
                httpResponseProcessor.handleResource(ctx, request, request.uri().substring(1));
                return false;
            }
            Map<String, List<String>> requestParams = new QueryStringDecoder(request.uri()).parameters();

            if (requestParams.isEmpty() || !requestParams.containsKey(HTTP_PARAM_REQUEST)) {
                httpResponseProcessor.handleResource(ctx, request, request.uri().substring(1));
                return false;
            }
            String jsonMessage = new String(Base64.getDecoder().decode(requestParams.get(HTTP_PARAM_REQUEST).get(0)), StandardCharsets.UTF_8);
            chatService.processMessage(jsonMessage, ctx.channel());
            return true;
        } catch (RuntimeException ex) {
            httpResponseProcessor.sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
            log.error("error at process Http request", ex);
            return false;
        }
    }


    @Override
    public void InitChannel(Channel channel) {
        chatService.InitChannel(channel);
    }

    @Override
    public void removeChannel(Channel channel) {
        chatService.removeChannel(channel);
    }
}
