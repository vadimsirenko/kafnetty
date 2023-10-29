package ru.vasire.kafnetty.server.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vasire.kafnetty.server.kafka.config.MyConsumer;

public class KafnettyConsumer {
    private static final Logger log = LoggerFactory.getLogger(KafnettyConsumer.class);
    public static void main(String[] args)  {
        var consumer = new MyConsumer("localhost:9092");
        var dataConsumer = new StringValueConsumer(consumer, value -> log.info("value:{}", value));
        dataConsumer.startConsuming();
    }
}
