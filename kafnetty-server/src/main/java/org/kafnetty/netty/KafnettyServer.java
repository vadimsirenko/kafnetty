package org.kafnetty.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafnettyServer {
    private final ServerBootstrap serverBootstrap;
    private final InetSocketAddress tcpPort;

    public void start() throws Exception {
        ChannelFuture f = serverBootstrap.bind(tcpPort).sync();
        f.channel().closeFuture().sync();
    }
}
