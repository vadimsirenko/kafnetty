package org.kafnetty.netty.handler.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.kafnetty.dto.BaseDto;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@Component
@RequiredArgsConstructor
@Slf4j
public class HttpProcessor {
    private static final Tika tika = new Tika();
    private final ResourceLoader resourceLoader;

    public void handleResource(ChannelHandlerContext ctx, HttpRequest request, String resourcePath) {
        int questionIndex = resourcePath.indexOf("?");
        if (questionIndex != -1) {
            resourcePath = resourcePath.substring(0, questionIndex);
        }
        if (resourcePath.startsWith("/")) {
            resourcePath = resourcePath.substring(1);
        }
        Resource resource = resourceLoader.getResource("classpath:" + resourcePath);
        InputStream inputStream;
        try {
            inputStream = resource.getInputStream();
            HttpHeaders headers;
            headers = getContentTypeHeader(resourcePath);
            // Write the initial line and the header.
            HttpResponse response = new DefaultHttpResponse(request.protocolVersion(), HttpResponseStatus.OK, headers);
            ctx.write(response);
            // Write the content and flush it.
            ByteBuffer buff = ByteBuffer.allocate(inputStream.available());
            buff.put(inputStream.readAllBytes());
            buff.flip();
            ctx.writeAndFlush(Unpooled.copiedBuffer(buff.array()));
            ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            future.addListener(ChannelFutureListener.CLOSE);
        } catch (IOException e) {
            sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
            log.error("error at process static resource", e);
        }
    }

    private HttpHeaders getContentTypeHeader(String resourcePath) {
        HttpHeaders headers = new DefaultHttpHeaders();
        String contentType = tika.detect(resourcePath);
        if (contentType.equals("text/plain")) {
            // Поскольку текст будет отображаться в браузере, он указан как кодирование UTF-8 здесь
            contentType = "text/plain;charset=utf-8";
        }
        headers.set(HttpHeaderNames.CONTENT_TYPE, contentType);
        return headers;
    }

    public void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest request, FullHttpResponse res) {
        if (res.status().code() != HttpResponseStatus.OK.code()) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            HttpUtil.setContentLength(res, res.content().readableBytes());
        }
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpUtil.isKeepAlive(request) || res.status().code() != HttpResponseStatus.OK.code()) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }
    public void sendHttpJsonResponse(ChannelHandlerContext ctx, HttpRequest request, HttpResponseStatus status, BaseDto baseDto) {
        ByteBuf buf = Unpooled.copiedBuffer(baseDto.toJson(), CharsetUtil.UTF_8);
        var response = new DefaultFullHttpResponse(HTTP_1_1, status);
        response.content().writeBytes(buf);
        HttpUtil.setContentLength(response, response.content().readableBytes());
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/json");
        sendHttpResponse(ctx, request, response);
    }
}
