package org.kafnetty.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.kafnetty.dto.BaseDto;
import org.kafnetty.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafnettyConsumer {
    private final ChatService chatService;

    @KafkaListener(topics = {"#{autoCreateTopicConfig.getTopic().split(',')}"},
            groupId = "#{kafnettyConsumerConfig.getGroupId()}")
    public void onMessage(ConsumerRecord<UUID, BaseDto> consumerRecord) {
        chatService.processBaseDtoFromKafka(consumerRecord);
        log.info("Consumer Record: {}", consumerRecord);
    }

    /**
     *  ----- consumer group and partition with intial offset  ----
     *
    @KafkaListener(groupId = "inventory-consumer-group-1",
            topicPartitions = @TopicPartition(topic = "inventory-events",
                    partitionOffsets = {
                            @PartitionOffset(partition = "0", initialOffset = "0"),
                            @PartitionOffset(partition = "2", initialOffset = "0")}))
     */
    public void onMessage_PartitionIntialOffset(ConsumerRecord<UUID, BaseDto> consumerRecord) {
        log.info("Consumer Record: {}", consumerRecord);
    }


    /**
     * ----- consumer group and partition with no intial offset  ----
     *
    @KafkaListener(topicPartitions = @TopicPartition(topic = "inventory-events", partitions = { "0", "1" }))
     */
    public void onMessage_PartitionNoOffset(ConsumerRecord<UUID, BaseDto> consumerRecord) {
        log.info("Consumer Record: {}", consumerRecord);
    }
}
