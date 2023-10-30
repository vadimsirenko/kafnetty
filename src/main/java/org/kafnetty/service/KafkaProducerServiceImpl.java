package org.kafnetty.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.kafnetty.dto.kafka.KafkaBaseDto;
import org.kafnetty.dto.kafka.KafkaMessageDto;
import org.kafnetty.kafka.config.JsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.CLIENT_ID_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.RETRIES_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.*;
import static org.kafnetty.kafka.config.JsonSerializer.OBJECT_MAPPER;

import java.util.Properties;
import java.util.UUID;
import java.util.function.Consumer;
@Component
public class KafkaProducerServiceImpl implements KafkaProducerService {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducerServiceImpl.class);

    @Value("${server.kafka.message-topic}")
    private String messageTopic;
    @Value("${server.kafka.room-topic}")
    private String roomTopic;
    @Value("${server.kafka.client-topic}")
    private String clientTopic;
    private KafkaProducer<UUID, KafkaBaseDto> kafkaProducer;

    private final Consumer<KafkaBaseDto> sendAsk =
            message->log.info("asked, value {}:{}", message.getMessageType(), message.getKafkaMessageId());

    public KafkaProducerServiceImpl(String clusterId, String bootstrapServers){
        Properties props = new Properties();
        props.put(CLIENT_ID_CONFIG, clusterId);
        props.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ACKS_CONFIG, "1");
        props.put(RETRIES_CONFIG, 1);
        props.put(BATCH_SIZE_CONFIG, 16384);
        props.put(LINGER_MS_CONFIG, 10);
        props.put(BUFFER_MEMORY_CONFIG, 33_554_432); //bytes
        props.put(MAX_BLOCK_MS_CONFIG, 1_000); //ms
        props.put(KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        props.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(OBJECT_MAPPER, new ObjectMapper());

        this.kafkaProducer = new KafkaProducer<UUID, KafkaBaseDto>(props);
        var shutdownHook = new Thread(() -> {
            //log.info("closing kafka producer");
            kafkaProducer.close();
        });
        Runtime.getRuntime().addShutdownHook(shutdownHook);

    }

/*
    private void initKafkaProducer() {
        Properties props = new Properties();
        props.put(CLIENT_ID_CONFIG, clusterId);
        props.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ACKS_CONFIG, "1");
        props.put(RETRIES_CONFIG, 1);
        props.put(BATCH_SIZE_CONFIG, 16384);
        props.put(LINGER_MS_CONFIG, 10);
        props.put(BUFFER_MEMORY_CONFIG, 33_554_432); //bytes
        props.put(MAX_BLOCK_MS_CONFIG, 1_000); //ms
        props.put(KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        props.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(OBJECT_MAPPER, new ObjectMapper());

        kafkaProducer = new KafkaProducer<>(props);

        var shutdownHook = new Thread(() -> {
            log.info("closing kafka producer");
            kafkaProducer.close();
        });
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }
*/
    @Override
    public void sendMessage(KafkaMessageDto kafkaMessageDto) {
        dataHandler(kafkaMessageDto, "kafnetty-messages");
        log.info("send message: " + kafkaMessageDto.getId());
       // log.info("servers: " + bootstrapServers);
    }

    public void dataHandler(KafkaBaseDto value, String topicName) {
/*
        log.info("value {}: {}", value.getMessageType(), value.toJson());
        try {
            kafkaProducer.send(new ProducerRecord<>(topicName, value.getKafkaMessageId(), value),
                    (metadata, exception) -> {
                        if (exception != null) {
                            log.error("message wasn't sent", exception);
                        } else {
                            log.info("message id:{} was sent, offset:{}", value.getKafkaMessageId(), metadata.offset());
                            sendAsk.accept(value);
                        }
                    });
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

 */
    }
}
