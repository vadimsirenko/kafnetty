package org.kafnetty.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kafnetty.dto.BaseDto;
import org.kafnetty.dto.ClientDto;
import org.kafnetty.dto.MessageDto;
import org.kafnetty.dto.RoomDto;
import org.kafnetty.kafka.config.KafnettyKafkaConfig;
import org.kafnetty.kafka.producer.KafkaProducerCallback;
import org.kafnetty.kafka.producer.KafnettyProducer;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerServiceImpl implements KafkaProducerService {
    private final KafnettyKafkaConfig kafnettyKafkaConfig;
    private final KafnettyProducer kafnettyProducer;
    @Override
    public void sendMessage(MessageDto channelMessageDto, KafkaProducerCallback kafkaProducerCallback) {
        channelMessageDto.setClusterId(kafnettyKafkaConfig.getGroupId());
        log.info("sendMessage {}: {}", channelMessageDto.getMessageType(), channelMessageDto.toJson());
        try {
            kafnettyProducer.create(channelMessageDto, kafkaProducerCallback);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void sendRoom(RoomDto channelRoomDto, KafkaProducerCallback kafkaProducerCallback) {
        channelRoomDto.setClusterId(kafnettyKafkaConfig.getGroupId());
        log.info("sendRoom {}: {}", channelRoomDto.getMessageType(), channelRoomDto.toJson());
        try {
            kafnettyProducer.create(channelRoomDto, kafkaProducerCallback);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void sendClient(ClientDto channelClientDto, KafkaProducerCallback kafkaProducerCallback) {
        channelClientDto.setClusterId(kafnettyKafkaConfig.getGroupId());
        log.info("sendClient {}: {}", channelClientDto.getMessageType(), channelClientDto.toJson());
        try {
            kafnettyProducer.create(channelClientDto, kafkaProducerCallback);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
