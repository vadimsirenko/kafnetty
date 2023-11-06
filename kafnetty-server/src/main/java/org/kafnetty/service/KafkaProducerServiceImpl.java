package org.kafnetty.service;

import lombok.RequiredArgsConstructor;
import org.kafnetty.dto.kafka.KafkaBaseDto;
import org.kafnetty.dto.kafka.KafkaMessageDto;
import org.kafnetty.dto.kafka.KafkaRoomDto;
import org.kafnetty.kafka.producer.KafnettyProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class KafkaProducerServiceImpl implements KafkaProducerService {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducerServiceImpl.class);
    private final KafnettyProducer kafkaProducer;

    private final Consumer<KafkaBaseDto> sendAsk =
            message -> log.info("asked, value {}:{}", message.getMessageType(), message.getKafkaMessageId());

    @Override
    public void sendMessage(KafkaMessageDto kafkaMessageDto, KafkaProducerCallback kafkaProducerCallback) {
        kafkaMessageDto.setClusterId(kafkaProducer.getGroupId());
        log.info("value {}: {}", kafkaMessageDto.getMessageType(), kafkaMessageDto.toJson());
        try {
            if (kafkaProducer.create(kafkaMessageDto)) {
                kafkaProducerCallback.run(kafkaMessageDto);
            }
            /*
            kafkaProducer.getKafkaProducer().send(new ProducerRecord<>(kafkaProducer.MESSAGE_TOPIC_NAME, kafkaMessageDto.getKafkaMessageId(), kafkaMessageDto),
                    (metadata, exception) -> {
                        if (exception != null) {
                            log.error("message wasn't sent", exception);
                        } else {
                            log.info("message id:{} was sent, offset:{}", kafkaMessageDto.getKafkaMessageId(), metadata.offset());
                            kafkaProducerCallback.run(kafkaMessageDto);
                        }
                    });

             */
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void sendRoom(KafkaRoomDto kafkaRoomDto, KafkaProducerCallback kafkaProducerCallback) {
        kafkaRoomDto.setClusterId(kafkaProducer.getGroupId());
        log.info("value {}: {}", kafkaRoomDto.getMessageType(), kafkaRoomDto.toJson());
        try {
            if (kafkaProducer.create(kafkaRoomDto)) {
                kafkaProducerCallback.run(kafkaRoomDto);
            }
            /*
            kafkaProducer.getKafkaProducer().send(new ProducerRecord<>(kafkaProducer.ROOM_TOPIC_NAME, kafkaRoomDto.getKafkaMessageId(), kafkaRoomDto),
                    (metadata, exception) -> {
                        if (exception != null) {
                            log.error("message wasn't sent", exception);
                        } else {
                            log.info("message id:{} was sent, offset:{}", kafkaRoomDto.getKafkaMessageId(), metadata.offset());
                            kafkaProducerCallback.run(kafkaRoomDto);
                        }
                    });*/
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
