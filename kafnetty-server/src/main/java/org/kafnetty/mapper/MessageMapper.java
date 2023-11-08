package org.kafnetty.mapper;

import org.kafnetty.dto.MessageDto;
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
    MessageDto MessageToChannelMessageDto(Message message);

    @Mapping(ignore = true, target = "sent")
    Message ChannelMessageDtoToMessage(MessageDto channelMessageDto);

    List<MessageDto> mapToChannelMessageDtoList(List<Message> clients);
}

