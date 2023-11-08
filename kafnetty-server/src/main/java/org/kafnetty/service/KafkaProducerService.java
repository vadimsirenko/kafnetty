package org.kafnetty.service;

import org.kafnetty.dto.ClientDto;
import org.kafnetty.dto.MessageDto;
import org.kafnetty.dto.RoomDto;
import org.kafnetty.kafka.producer.KafkaProducerCallback;

public interface KafkaProducerService {
    void sendMessage(MessageDto channelMessageDto, KafkaProducerCallback kafkaProducerCallback);

    void sendRoom(RoomDto channelRoomDto, KafkaProducerCallback kafkaProducerCallback);

    void sendClient(ClientDto channelClientDto, KafkaProducerCallback kafkaProducerCallback);
}
