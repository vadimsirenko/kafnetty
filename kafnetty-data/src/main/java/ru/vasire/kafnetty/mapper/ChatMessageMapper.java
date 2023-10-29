package ru.vasire.kafnetty.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import ru.vasire.kafnetty.dto.ChatMessageDto;
import ru.vasire.kafnetty.entity.ChatMessage;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChatMessageMapper {
    ChatMessageMapper INSTANCE = Mappers.getMapper( ChatMessageMapper.class );
    @Mapping(ignore = true, target = "messageType")
    @Mapping(ignore = true, target = "operationType")
    ChatMessageDto ChatMessageToChatMessageDto(ChatMessage chatMessage);

    ChatMessage ChatMessageDtoToChatMessage(ChatMessageDto chatMessageDto);
}

