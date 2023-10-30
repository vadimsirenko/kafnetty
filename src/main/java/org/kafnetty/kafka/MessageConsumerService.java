package org.kafnetty.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.kafnetty.dto.kafka.KafkaBaseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.kafnetty.kafka.MessageKafkaConsumer.MAX_POLL_INTERVAL_MS;

@Service
public class MessageConsumerService {
    private static final Logger log = LoggerFactory.getLogger(MessageConsumerService.class);

    private final MessageKafkaConsumer messageKafkaConsumer;
    private final Duration timeout = Duration.ofMillis(2_000);
    private final Consumer<KafkaBaseDto> dataConsumer;
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    public MessageConsumerService(MessageKafkaConsumer messageKafkaConsumer){ //}, Consumer<KafkaBaseDto> dataConsumer) {
        this.dataConsumer = value -> log.info("value:{}", value);
        this.messageKafkaConsumer = messageKafkaConsumer;
    }

    public void startConsuming() {
        // executor.scheduleAtFixedRate(this::poll, 0, MAX_POLL_INTERVAL_MS * 2L, TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(this::poll, 0, MAX_POLL_INTERVAL_MS / 2L, TimeUnit.MILLISECONDS);
    }

    private void poll() {
        log.info("poll records");
        ConsumerRecords<UUID, KafkaBaseDto> records = messageKafkaConsumer.getKafkaConsumer().poll(timeout);
        //       sleep();
        log.info("polled records.counter:{}", records.count());
        for (ConsumerRecord<UUID, KafkaBaseDto> kafkaRecord : records) {
            try {
                var key = kafkaRecord.key();
                var value = kafkaRecord.value();
                log.info("key:{}, value:{}, record:{}", key, value, kafkaRecord);
                dataConsumer.accept(value);
            } catch (Exception ex) {
                log.error("can't parse record:{}", kafkaRecord, ex);
            }
        }
    }

    public void stopSending() {
        executor.shutdown();
    }

    private void sleep() {
        try {
            Thread.sleep(MAX_POLL_INTERVAL_MS * 2L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
