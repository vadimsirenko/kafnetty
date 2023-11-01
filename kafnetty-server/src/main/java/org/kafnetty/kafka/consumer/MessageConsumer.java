package org.kafnetty.kafka.consumer;

import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Collections;
@Component
public class MessageConsumer extends BaseConsumer {
    @Autowired
    private ApplicationContext context;
    @PostConstruct
    @Override
    public void init() {
        super.init();
        kafkaConsumer = new KafkaConsumer<>(props);
        kafkaConsumer.subscribe(Collections.singletonList(MESSAGE_TOPIC_NAME));
    }
}
