package org.kafnetty.kafka.producer;

import org.kafnetty.dto.kafka.KafkaBaseDto;

public interface KafnettyProducer {
    boolean create(KafkaBaseDto kafkaBaseDto, KafkaProducerCallback successCallback);
}
