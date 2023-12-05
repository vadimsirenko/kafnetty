package org.kafnetty.service;

import io.netty.channel.Channel;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.kafnetty.dto.BaseDto;

import java.util.UUID;

public interface ChatService {

    void removeChannel(Channel channel);
    void putChannel(UUID roomId, Channel channel);

    void processMessage(BaseDto messageDto, Channel channel);

    boolean existsChannelUser(Channel channel);

    void InitChannel(Channel channel);

    void processBaseDtoFromKafka(ConsumerRecord<UUID, BaseDto> consumerRecord);
}
