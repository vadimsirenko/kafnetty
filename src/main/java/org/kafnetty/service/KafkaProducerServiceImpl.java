package org.kafnetty.service;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.kafnetty.dto.kafka.KafkaBaseDto;
import org.kafnetty.dto.kafka.KafkaMessageDto;
import org.kafnetty.kafka.KafnettyKafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.Consumer;
@Service
@RequiredArgsConstructor
public class KafkaProducerServiceImpl implements KafkaProducerService {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducerServiceImpl.class);
    private final KafnettyKafkaProducer kafkaProducer;

    private final Consumer<KafkaBaseDto> sendAsk =
            message->log.info("asked, value {}:{}", message.getMessageType(), message.getKafkaMessageId());

    @Override
    public void sendMessage(KafkaMessageDto kafkaMessageDto, KafkaProducerCallback kafkaProducerCallback) {
        kafkaMessageDto.setClusterId(kafkaProducer.CLUSTER_ID);
        log.info("value {}: {}", kafkaMessageDto.getMessageType(), kafkaMessageDto.toJson());
        try {
            kafkaProducer.getKafkaProducer().send(new ProducerRecord<>(kafkaProducer.MESSAGE_TOPIC_NAME, kafkaMessageDto.getKafkaMessageId(), kafkaMessageDto),
                    (metadata, exception) -> {
                        if (exception != null) {
                            log.error("message wasn't sent", exception);
                        } else {
                            log.info("message id:{} was sent, offset:{}", kafkaMessageDto.getKafkaMessageId(), metadata.offset());
                            kafkaProducerCallback.run(kafkaMessageDto);
                        }
                    });
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
