package org.kafnetty.service;

import org.kafnetty.dto.kafka.KafkaMessageDto;

public interface KafkaProducerService {
    public interface KafkaProducerCallback{ void run(KafkaMessageDto kafkaMessageDto); }
    void sendMessage(KafkaMessageDto kafkaMessageDto, KafkaProducerCallback kafkaProducerCallback);
}
