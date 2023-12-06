package org.kafnetty;

import org.kafnetty.netty.KafnettyServer;
import org.kafnetty.netty.config.ServerConstants;
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
        log.info("_____________________________________________");
        log.info("Open chat at url http://localhost:{}{}", port, ServerConstants.CHAT_PATH);
        log.info("using logins: oleg, vadim, sergey");
        log.info("_____________________________________________");
        ctx.getBean(KafnettyServer.class).start();
    }
}
