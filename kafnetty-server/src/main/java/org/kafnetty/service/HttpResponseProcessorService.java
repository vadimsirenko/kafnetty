package org.kafnetty.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;

public interface HttpResponseProcessorService {
    void handleResource(ChannelHandlerContext ctx, HttpRequest request, String resourcePath);

    void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req, FullHttpResponse res);
}
