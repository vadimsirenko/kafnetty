package org.kafnetty;

import org.kafnetty.netty.KafnettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = "org.kafnetty")
public class KafnettyApplication {
    private static final Logger log = LoggerFactory.getLogger(KafnettyApplication.class);

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext ctx = SpringApplication.run(KafnettyApplication.class, args);
        String port = ctx.getEnvironment().getProperty("server.port");
        String chatPath = ctx.getEnvironment().getProperty("server.chat-path");
        log.info("_____________________________________________");
        log.info("Open chat at url http://localhost:{}{}", port, chatPath);
        log.info("using logins: oleg, vadim, sergey");
        log.info("_____________________________________________");
        ctx.getBean(KafnettyServer.class).start();
    }
}
