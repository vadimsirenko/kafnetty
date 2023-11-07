package org.kafnetty.mapper;

import org.kafnetty.dto.channel.ChannelClientDto;
import org.kafnetty.dto.channel.ChannelMessageDto;
import org.kafnetty.dto.kafka.KafkaMessageDto;
import org.kafnetty.entity.Client;
import org.kafnetty.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MessageMapper {
    MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);

    @Mapping(ignore = true, target = "messageType")
    @Mapping(ignore = true, target = "operationType")
    ChannelMessageDto MessageToChannelMessageDto(Message message);

    @Mapping(ignore = true, target = "sent")
    Message ChannelMessageDtoToMessage(ChannelMessageDto channelMessageDto);

    List<ChannelMessageDto> mapToChannelMessageDtoList(List<Message> clients);

    @Mapping(ignore = true, target = "kafkaMessageId")
    @Mapping(ignore = true, target = "clusterId")
    KafkaMessageDto ChannelMessageDtoToKafkaMessageDto(ChannelMessageDto channelMessageDto);

    ChannelMessageDto KafkaMessageDtoToChannelMessageDto(KafkaMessageDto kafkaMessageDto);
}

