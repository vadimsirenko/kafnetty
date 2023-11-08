package org.kafnetty.kafka.producer;

import org.kafnetty.dto.BaseDto;

public interface KafnettyProducer {
    void create(BaseDto kafkaBaseDto, KafkaProducerCallback successCallback);
}
