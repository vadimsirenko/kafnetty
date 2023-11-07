package org.kafnetty.kafka.producer;

import io.micrometer.observation.annotation.Observed;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kafnetty.dto.kafka.KafkaBaseDto;
import org.kafnetty.kafka.config.KafnettyKafkaConfig;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@RequiredArgsConstructor
@Slf4j
@Observed
@Getter
public class KafnettyProducerImpl implements KafnettyProducer {
    private final KafnettyKafkaConfig kafnettyKafkaConfig;
    private final KafkaTemplate<String, KafkaBaseDto> kafkaTemplate;

    @Override
    public boolean create(KafkaBaseDto kafkaBaseDto, KafkaProducerCallback successCallback) {
        log.info("Attempting to log {} to topic {}.", kafkaBaseDto, kafnettyKafkaConfig.getTopicName());
        final String key = kafkaBaseDto.getKafkaMessageId().toString();
        try {
            SendResult<String, KafkaBaseDto> result = kafkaTemplate
                    .send(kafnettyKafkaConfig.getTopicName(), key, kafkaBaseDto)
                    .get(10, TimeUnit.SECONDS);
            onSuccess(result, successCallback);
            return true;
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            onFailure(e);
            return false;
        }
    }

    private void onSuccess(final SendResult<String, KafkaBaseDto> result, KafkaProducerCallback successCallback) {
        log.info("Payment '{}' has been written to topic-partition {}-{} with ingestion timestamp {}.",
                result.getProducerRecord().key(),
                result.getRecordMetadata().topic(),
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().timestamp());
        successCallback.run(result.getProducerRecord().value());
    }

    private void onFailure(final Throwable t) {
        log.warn("Unable to write message to topic {}.", kafnettyKafkaConfig.getTopicName(), t);
    }
}