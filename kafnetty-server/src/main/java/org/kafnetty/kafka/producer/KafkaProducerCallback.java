package org.kafnetty.kafka.producer;

import org.kafnetty.dto.kafka.KafkaBaseDto;

public interface KafkaProducerCallback {
    void run(KafkaBaseDto kafkaDto);
}