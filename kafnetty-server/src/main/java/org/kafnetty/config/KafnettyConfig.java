package org.kafnetty.config;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Configuration()
public class KafnettyConfig {
    @Value("${server.port}")
    private int tcpPort;

    @Value("${server.boss.thread.count}")
    private int bossCount;

    @Value("${server.worker.thread.count}")
    private int workerCount;

    @Value("${server.so.keepalive}")
    private boolean keepAlive;

    @Value("${server.so.backlog}")
    private int backlog;
    private final ChannelHandler webSocketServerInitializer;

    public KafnettyConfig(@Qualifier("webSocketServerInitializer") ChannelHandler webSocketServerInitializer) {
        this.webSocketServerInitializer = webSocketServerInitializer;
    }

    @Bean(name = "serverBootstrap")
    public ServerBootstrap bootstrap() {
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup(), workerGroup())
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(webSocketServerInitializer);
        b.option(ChannelOption.SO_KEEPALIVE, keepAlive);
        b.option(ChannelOption.SO_BACKLOG, backlog);
        return b;
    }

    @Bean(name = "bossGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup bossGroup() {
        return new NioEventLoopGroup(bossCount);
    }

    @Bean(name = "workerGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup workerGroup() {
        return new NioEventLoopGroup(workerCount);
    }

    @Bean(name = "tcpSocketAddress")
    public InetSocketAddress tcpPort() {
        return new InetSocketAddress(tcpPort);
    }
}
