package org.kafnetty.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.KafkaClient;
import org.kafnetty.dto.kafka.KafkaBaseDto;
import org.kafnetty.dto.kafka.KafkaClientDto;
import org.kafnetty.dto.kafka.KafkaMessageDto;
import org.kafnetty.dto.kafka.KafkaRoomDto;
import org.kafnetty.kafka.producer.KafnettyProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerServiceImpl implements KafkaProducerService {
    private final KafnettyProducer kafkaProducer;
    private final Consumer<KafkaBaseDto> sendAsk =
            message -> log.info("asked, value {}:{}", message.getMessageType(), message.getKafkaMessageId());
    @Override
    public void sendMessage(KafkaMessageDto kafkaMessageDto, KafkaProducerCallback kafkaProducerCallback) {
        kafkaMessageDto.setClusterId(kafkaProducer.getGroupId());
        log.info("sendMessage {}: {}", kafkaMessageDto.getMessageType(), kafkaMessageDto.toJson());
        try {
            if (kafkaProducer.create(kafkaMessageDto)) {
                kafkaProducerCallback.run(kafkaMessageDto);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
    @Override
    public void sendRoom(KafkaRoomDto kafkaRoomDto, KafkaProducerCallback kafkaProducerCallback) {
        kafkaRoomDto.setClusterId(kafkaProducer.getGroupId());
        log.info("sendRoom {}: {}", kafkaRoomDto.getMessageType(), kafkaRoomDto.toJson());
        try {
            if (kafkaProducer.create(kafkaRoomDto)) {
                kafkaProducerCallback.run(kafkaRoomDto);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
    @Override
    public void sendClient(KafkaClientDto kafkaClientDto, KafkaProducerCallback kafkaProducerCallback) {
        kafkaClientDto.setClusterId(kafkaProducer.getGroupId());
        log.info("sendClient {}: {}", kafkaClientDto.getMessageType(), kafkaClientDto.toJson());
        try {
            if (kafkaProducer.create(kafkaClientDto)) {
                kafkaProducerCallback.run(kafkaClientDto);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
