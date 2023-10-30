package org.kafnetty.service;

import org.kafnetty.dto.kafka.KafkaMessageDto;

public interface KafkaProducerService {
    void sendMessage(KafkaMessageDto kafkaMessageDto);
}
