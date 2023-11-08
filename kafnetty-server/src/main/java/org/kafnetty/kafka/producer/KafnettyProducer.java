package org.kafnetty.kafka.producer;

import org.kafnetty.dto.BaseDto;

public interface KafnettyProducer {
    boolean create(BaseDto kafkaBaseDto, KafkaProducerCallback successCallback);
}
