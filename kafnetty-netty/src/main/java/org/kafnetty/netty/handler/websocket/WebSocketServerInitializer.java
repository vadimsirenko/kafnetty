package org.kafnetty.netty.handler.websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kafnetty.netty.handler.auth.JwtContextResolverHandler;
import org.kafnetty.netty.handler.auth.AuthenticationHandler;
import org.kafnetty.netty.handler.http.HttpServerHandler;
import org.kafnetty.netty.handler.http.StaticContentHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("webSocketServerInitializer")
@RequiredArgsConstructor
@Slf4j
public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {
    private final HttpServerHandler httpServerHandler;
    private final WebSocketFrameHandler webSocketFrameHandler;
    private final AuthenticationHandler authenticationHandler;
    private final JwtContextResolverHandler jwtContextResolverHandler;
    private final StaticContentHandler staticContentHandler;


    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new WebSocketServerCompressionHandler());
        pipeline.addLast(staticContentHandler);
        pipeline.addLast(authenticationHandler);
        pipeline.addLast(jwtContextResolverHandler);
        pipeline.addLast(httpServerHandler);
        pipeline.addLast(webSocketFrameHandler);
    }
}
