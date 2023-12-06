package org.kafnetty.netty.handler.http;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.kafnetty.netty.handler.http.HttpProcessor;
import org.kafnetty.netty.handler.BaseWebSocketServerHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static org.kafnetty.config.ServerConstants.*;
import static org.kafnetty.config.ServerConstants.CHAT_PATH;

@Component
@ChannelHandler.Sharable
@Qualifier("staticContentHandler")
@Slf4j
public class StaticContentHandler extends BaseWebSocketServerHandler<FullHttpRequest> {
    private final HttpProcessor httpProcessor;

    public StaticContentHandler(HttpProcessor httpProcessor) {
        super(false);
        this.httpProcessor = httpProcessor;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        try {
            // Handle a bad request.
            if (!request.decoderResult().isSuccess()) {
                httpProcessor.sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
                ctx.fireChannelReadComplete();
                return;
            }
            // GET methods.
            else if (request.method() == GET){
                if ("/favicon.ico".equals(request.uri()) || "/".equals(request.uri())) {
                    System.out.println("not fount! : " + request.uri());
                    httpProcessor.sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
                    ctx.fireChannelReadComplete();
                    return;
                }
                if (request.uri().startsWith(STATIC_CONTENT_PATH) || request.uri().startsWith(CHAT_PATH)) {
                    httpProcessor.sendContentResponse(ctx, request, request.uri().substring(1));
                    ctx.fireChannelReadComplete();
                    return;
                }
            }
            ctx.fireChannelRead(request);
        } catch (Exception ex) {
            httpProcessor.sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
            log.error("error at process Http request", ex);
            ctx.fireChannelReadComplete();
        }
    }
}
