package ru.vasire.kafnetty.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.vasire.kafnetty.server.netty.TCPServer;

@SpringBootApplication(scanBasePackages = "ru.vasire.kafnetty")
public class Application {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);
        ctx.getBean(TCPServer.class).start();
    }
}
