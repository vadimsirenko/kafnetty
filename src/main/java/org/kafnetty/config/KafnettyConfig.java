package org.kafnetty.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.kafnetty.dto.kafka.KafkaBaseDto;
import org.kafnetty.kafka.config.JsonSerializer;
import org.kafnetty.kafka.config.TopicConfig;
import org.kafnetty.service.KafkaProducerService;
import org.kafnetty.service.KafkaProducerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.util.*;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.CLIENT_ID_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.RETRIES_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.*;
import static org.kafnetty.kafka.config.JsonSerializer.OBJECT_MAPPER;

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
    @Autowired
    @Qualifier("webSocketServerInitializer")
    private ChannelHandler webSocketServerInitializer;

    @Bean(name = "serverBootstrap")
    public ServerBootstrap bootstrap() {
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup(), workerGroup())
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(webSocketServerInitializer);
        Map<ChannelOption<?>, Object> tcpChannelOptions = tcpChannelOptions();
        Set<ChannelOption<?>> keySet = tcpChannelOptions.keySet();
        for (@SuppressWarnings("rawtypes") ChannelOption option : keySet) {
            b.option(option, tcpChannelOptions.get(option));
        }
        return b;
    }

    @Bean(name = "tcpChannelOptions")
    public Map<ChannelOption<?>, Object> tcpChannelOptions() {
        Map<ChannelOption<?>, Object> options = new HashMap<ChannelOption<?>, Object>();
        options.put(ChannelOption.SO_KEEPALIVE, keepAlive);
        options.put(ChannelOption.SO_BACKLOG, backlog);
        return options;
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

    @Value("${server.kafka.servers}")
    private String bootstrapServers;
    @Value("${server.cluster-id}")
    private String clusterId;
    @Value("${server.kafka.message-topic}")
    private String messageTopic;
    @Value("${server.kafka.room-topic}")
    private String roomTopic;
    @Value("${server.kafka.client-topic}")
    private String clientTopic;
    @Bean(name = "kafkaProducerService")
    public KafkaProducerService kafkaProducerService() {
        return new KafkaProducerServiceImpl(clusterId, bootstrapServers);
    }

}
