package org.kafnetty.service;

import org.kafnetty.dto.kafka.KafkaBaseDto;
import org.kafnetty.dto.kafka.KafkaMessageDto;
import org.kafnetty.dto.kafka.KafkaRoomDto;

public interface KafkaProducerService {
    public interface KafkaProducerCallback {
        void run(KafkaBaseDto kafkaDto);
    }

    void sendMessage(KafkaMessageDto kafkaMessageDto, KafkaProducerCallback kafkaProducerCallback);

    void sendRoom(KafkaRoomDto kafkaRoomDto, KafkaProducerCallback kafkaProducerCallback);
}
