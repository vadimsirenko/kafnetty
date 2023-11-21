package org.kafnetty.netty.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kafnetty.netty.handler.http.HttpServerHandler;
import org.kafnetty.netty.handler.websocket.CloseWebSocketHandler;
import org.kafnetty.netty.handler.websocket.PingPongWebSocketHandler;
import org.kafnetty.netty.handler.websocket.TextWebSocketHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("webSocketServerInitializer")
@RequiredArgsConstructor
@Slf4j
public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {
    private final HttpServerHandler httpServerHandler;
    private final TextWebSocketHandler textWebSocketHandler;
    private final PingPongWebSocketHandler pingPongWebsocketHandler;
    private final CloseWebSocketHandler closeWebSocketHandler;


    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new WebSocketServerCompressionHandler());
        pipeline.addLast(pingPongWebsocketHandler);
        pipeline.addLast(httpServerHandler);
        pipeline.addLast(closeWebSocketHandler);
        pipeline.addLast(textWebSocketHandler);
    }
}
