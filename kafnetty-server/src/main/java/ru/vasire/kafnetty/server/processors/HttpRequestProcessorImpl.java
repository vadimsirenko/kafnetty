package ru.vasire.kafnetty.server.processors;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.vasire.kafnetty.server.dto.ErrorDto;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@Component
@RequiredArgsConstructor
public class HttpRequestProcessorImpl implements HttpRequestProcessor {
    private final ChatProcessor chatProcessor;
    @Override
    public void processWebSocketRequest(Channel channel, WebSocketFrame frame) {
        try {
            if (!chatProcessor.existsUserProfile(channel)) {
                channel.writeAndFlush(ErrorDto.createCommonError("You can't chat without logging in").toWebSocketFrame());
            } else {
                chatProcessor.processMessage(((TextWebSocketFrame) frame).text(), channel);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean processHttpRequest(ChannelHandlerContext ctx, HttpRequest request) {
        try {
            // Handle a bad request.
            if (!request.decoderResult().isSuccess()) {
                sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
                return false;
            }

            // Allow only GET methods.
            if (request.method() != GET) {
                sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
                return false;
            }

            if ("/favicon.ico".equals(request.uri()) || "/".equals(request.uri())) {
                sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
                return false;
            }

            Map<String, List<String>> requestParams = new QueryStringDecoder(request.uri()).parameters();

            if (requestParams.isEmpty() || !requestParams.containsKey(HTTP_PARAM_REQUEST)) {
                System.err.println(HTTP_PARAM_REQUEST + " parameters are not default");
                sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
                return false;
            }
            String jsonMessage = new String(Base64.getDecoder().decode(requestParams.get(HTTP_PARAM_REQUEST).get(0)), StandardCharsets.UTF_8);
            chatProcessor.processMessage(jsonMessage, ctx.channel());
            return true;
        } catch (RuntimeException ex) {
            sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
            System.err.println(ex.getMessage());
            return false;
        }
    }
    private void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req, FullHttpResponse res) {
        if (res.status().code() != HttpResponseStatus.OK.code()) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            HttpUtil.setContentLength(res, res.content().readableBytes());
        }
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpUtil.isKeepAlive(req) || res.status().code() != HttpResponseStatus.OK.code()) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }
    @Override
    public void InitChannel(Channel channel) {
        chatProcessor.InitChannel(channel);
    }

    @Override
    public void removeChannel(Channel channel) {
        chatProcessor.removeChannel(channel);
    }
}
