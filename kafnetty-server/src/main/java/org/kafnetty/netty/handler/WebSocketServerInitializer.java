package org.kafnetty.netty.handler;

import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("webSocketServerInitializer")
@Slf4j
public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {
    private final ChannelInboundHandlerAdapter channelInboundHandlerAdapter;

    public WebSocketServerInitializer(@Qualifier("webSocketServerHandler") ChannelInboundHandlerAdapter channelInboundHandlerAdapter) {
        this.channelInboundHandlerAdapter = channelInboundHandlerAdapter;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new WebSocketServerCompressionHandler());
        pipeline.addLast(channelInboundHandlerAdapter);
    }
}
