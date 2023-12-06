package org.kafnetty.service.chat;

import io.netty.channel.Channel;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.kafnetty.dto.BaseDto;

import java.util.UUID;

public interface ChatService {

    void removeChannel(Channel channel);
    void putChannel(UUID roomId, Channel channel);

    void processMessage(BaseDto messageDto, Channel channel, String groupId);

    void InitChannel(Channel channel);

    void processBaseDtoFromKafka(ConsumerRecord<UUID, BaseDto> consumerRecord, String groupId);
}
