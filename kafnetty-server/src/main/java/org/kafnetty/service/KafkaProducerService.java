package org.kafnetty.service;

import org.kafnetty.dto.kafka.KafkaClientDto;
import org.kafnetty.dto.kafka.KafkaMessageDto;
import org.kafnetty.dto.kafka.KafkaRoomDto;
import org.kafnetty.kafka.producer.KafkaProducerCallback;

public interface KafkaProducerService {
    void sendMessage(KafkaMessageDto kafkaMessageDto, KafkaProducerCallback kafkaProducerCallback);

    void sendRoom(KafkaRoomDto kafkaRoomDto, KafkaProducerCallback kafkaProducerCallback);

    void sendClient(KafkaClientDto kafkaClientDto, KafkaProducerCallback kafkaProducerCallback);
}
