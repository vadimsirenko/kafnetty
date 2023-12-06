package org.kafnetty.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.kafnetty.dto.BaseDto;
import org.kafnetty.service.KafnettyProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Slf4j
@Component
public class KafnettyProducerImpl implements KafnettyProducer {
    @Value("${spring.kafka.topic}")
    public String topic;

    @Autowired
    private KafkaTemplate<UUID, BaseDto> kafkaTemplate;
    public CompletableFuture<SendResult<UUID, BaseDto>> sendMessage(BaseDto baseDto, Consumer<BaseDto> successCallback) {
        var key = baseDto.getId();
        var producerRecord = buildProducerRecord(key, baseDto);
        var completableFuture = kafkaTemplate.send(producerRecord);
        return completableFuture.whenComplete(((sendResult, throwable) -> {
            if (throwable != null) {
                handleFailure(key, baseDto, throwable);
            } else {
                successCallback.accept(baseDto);
            }
        }));
    }
    private ProducerRecord<UUID, BaseDto> buildProducerRecord(UUID key, BaseDto value) {
        List<Header> recordHeader = List.of(new RecordHeader("event-source", "kafnetty-producer".getBytes()));
        return new ProducerRecord<>(topic, null, key, value, recordHeader);
    }
    private void handleFailure(UUID key, BaseDto value, Throwable throwable) {
        log.error("Error sending message and exception is {}", throwable.getMessage(), throwable);
    }
}
