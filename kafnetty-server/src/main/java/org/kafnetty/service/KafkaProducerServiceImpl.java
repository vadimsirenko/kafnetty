package org.kafnetty.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kafnetty.dto.kafka.KafkaBaseDto;
import org.kafnetty.dto.kafka.KafkaClientDto;
import org.kafnetty.dto.kafka.KafkaMessageDto;
import org.kafnetty.dto.kafka.KafkaRoomDto;
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
    private final Consumer<KafkaBaseDto> sendAsk =
            message -> log.info("asked, value {}:{}", message.getMessageType(), message.getKafkaMessageId());

    @Override
    public void sendMessage(KafkaMessageDto kafkaMessageDto, KafkaProducerCallback kafkaProducerCallback) {
        kafkaMessageDto.setClusterId(kafnettyKafkaConfig.getGroupId());
        log.info("sendMessage {}: {}", kafkaMessageDto.getMessageType(), kafkaMessageDto.toJson());
        try {
            kafnettyProducer.create(kafkaMessageDto, kafkaProducerCallback);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void sendRoom(KafkaRoomDto kafkaRoomDto, KafkaProducerCallback kafkaProducerCallback) {
        kafkaRoomDto.setClusterId(kafnettyKafkaConfig.getGroupId());
        log.info("sendRoom {}: {}", kafkaRoomDto.getMessageType(), kafkaRoomDto.toJson());
        try {
            kafnettyProducer.create(kafkaRoomDto, kafkaProducerCallback);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void sendClient(KafkaClientDto kafkaClientDto, KafkaProducerCallback kafkaProducerCallback) {
        kafkaClientDto.setClusterId(kafnettyKafkaConfig.getGroupId());
        log.info("sendClient {}: {}", kafkaClientDto.getMessageType(), kafkaClientDto.toJson());
        try {
            kafnettyProducer.create(kafkaClientDto, kafkaProducerCallback);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
