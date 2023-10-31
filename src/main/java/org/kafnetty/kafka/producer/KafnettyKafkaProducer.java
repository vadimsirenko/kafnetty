package org.kafnetty.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.UUIDSerializer;
import org.kafnetty.dto.kafka.KafkaBaseDto;
import org.kafnetty.kafka.config.JsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.UUID;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.CLIENT_ID_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.RETRIES_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.*;
import static org.kafnetty.kafka.config.JsonSerializer.OBJECT_MAPPER;

@Component()
public class KafnettyKafkaProducer {

    @Autowired
    private ApplicationContext context;

    @Value("${server.kafka.message-topic}")
    public String MESSAGE_TOPIC_NAME;
    @Value("${server.kafka.room-topic}")
    public String ROOM_TOPIC_NAME;
    @Value("${server.kafka.client-topic}")
    public String CLIENT_TOPIC_NAME;
    @Value("${server.kafka.servers}")
    public String BOOTSRTAP_SEVERS;
    @Value("${server.cluster-id}")
    public String CLUSTER_ID;
    private static final Logger log = LoggerFactory.getLogger(KafnettyKafkaProducer.class);
    @Getter
    private KafkaProducer<UUID, KafkaBaseDto> kafkaProducer;

    @PostConstruct
    public void init() {
        Properties props = new Properties();
        props.put(CLIENT_ID_CONFIG, CLUSTER_ID);
        props.put(BOOTSTRAP_SERVERS_CONFIG, BOOTSRTAP_SEVERS);
        props.put(ACKS_CONFIG, "1");
        props.put(RETRIES_CONFIG, 1);
        props.put(BATCH_SIZE_CONFIG, 16384);
        props.put(LINGER_MS_CONFIG, 10);
        props.put(BUFFER_MEMORY_CONFIG, 33_554_432); //bytes
        props.put(MAX_BLOCK_MS_CONFIG, 1_000); //ms
        props.put(KEY_SERIALIZER_CLASS_CONFIG, UUIDSerializer.class);
        props.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(OBJECT_MAPPER, new ObjectMapper());

        kafkaProducer = new KafkaProducer<>(props);

        var shutdownHook = new Thread(() -> {
            log.info("closing kafka producer");
            kafkaProducer.close();
        });
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

}
