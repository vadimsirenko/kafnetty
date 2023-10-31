package org.kafnetty.kafka.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.UUIDDeserializer;
import org.kafnetty.dto.kafka.KafkaBaseDto;
import org.kafnetty.exception.ConsumerException;
import org.kafnetty.kafka.config.JsonDeserializer;
import org.springframework.beans.factory.annotation.Value;

import java.net.InetAddress;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import static org.apache.kafka.clients.CommonClientConfigs.GROUP_INSTANCE_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

public class BaseConsumer {

    public static final int MAX_POLL_INTERVAL_MS = 10000;//300;

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
    @Value("${server.cluster-id}")
    public String GROUP_ID_CONFIG_NAME;
    @Getter
    protected KafkaConsumer<UUID, KafkaBaseDto> kafkaConsumer;
    private final Random random = new Random();
    protected final Properties props = new Properties();

    @PostConstruct
    public void init() {
        props.put(BOOTSTRAP_SERVERS_CONFIG, BOOTSRTAP_SEVERS);
        props.put(GROUP_ID_CONFIG, GROUP_ID_CONFIG_NAME);
        //props.put(GROUP_INSTANCE_ID_CONFIG, GROUP_ID_CONFIG_NAME);
        props.put(GROUP_INSTANCE_ID_CONFIG, makeGroupInstanceIdConfig());
        props.put(ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        props.put(AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(KEY_DESERIALIZER_CLASS_CONFIG, UUIDDeserializer.class);
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.OBJECT_MAPPER, new ObjectMapper());
        props.put(JsonDeserializer.TYPE_REFERENCE, new TypeReference<KafkaBaseDto>() {
        });

        props.put(MAX_POLL_RECORDS_CONFIG, 3);
        props.put(MAX_POLL_INTERVAL_MS_CONFIG, MAX_POLL_INTERVAL_MS);
    }
    private String makeGroupInstanceIdConfig() {
        try {
            var hostName = InetAddress.getLocalHost().getHostName();
            return String.join("-", GROUP_ID_CONFIG_NAME, hostName, String.valueOf(random.nextInt(100_999_999)));
        } catch (Exception ex) {
            throw new ConsumerException("can't make GroupInstanceIdConfig", ex);
        }
    }
}
