package org.kafnetty;

import org.kafnetty.kafka.MessageConsumerService;
import org.kafnetty.kafka.MessageKafkaConsumer;
import org.kafnetty.netty.TCPServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = "org.kafnetty")
public class ApplicationConsumer {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext ctx = SpringApplication.run(ApplicationConsumer.class, args);
        ctx.getBean(MessageConsumerService.class).startConsuming();
    }
}
