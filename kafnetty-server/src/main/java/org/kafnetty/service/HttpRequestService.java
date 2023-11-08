package org.kafnetty.service;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public interface HttpRequestService {
    String HTTP_PARAM_REQUEST = "request";

    void processWebSocketRequest(Channel channel, WebSocketFrame frame);

    boolean processHttpRequest(ChannelHandlerContext ctx, HttpRequest request);

    void InitChannel(Channel channel);

    void removeChannel(Channel channel);
}
