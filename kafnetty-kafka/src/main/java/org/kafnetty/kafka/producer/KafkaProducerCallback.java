package org.kafnetty.kafka.producer;


import org.kafnetty.dto.BaseDto;

public interface KafkaProducerCallback {
    void run(BaseDto kafkaDto);
}