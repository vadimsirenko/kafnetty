package org.kafnetty;

import org.kafnetty.kafka.consumer.MessageConsumerService;
import org.kafnetty.netty.TCPServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = "org.kafnetty")
public class Application {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);
        ctx.getBean(MessageConsumerService.class).startConsuming();
        ctx.getBean(TCPServer.class).start();
    }
}
