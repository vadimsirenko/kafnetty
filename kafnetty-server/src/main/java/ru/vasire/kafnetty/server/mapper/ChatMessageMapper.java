package ru.vasire.kafnetty.server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.vasire.kafnetty.server.dto.ChatMessageDto;
import ru.vasire.kafnetty.server.entity.ChatMessage;

@Mapper
public interface ChatMessageMapper {
    ChatMessageMapper INSTANCE = Mappers.getMapper( ChatMessageMapper.class );
    @Mapping(ignore = true, target = "messageType")
    @Mapping(ignore = true, target = "operationType")
    ChatMessageDto ChatMessageToChatMessageDto(ChatMessage chatMessage);

    ChatMessage ChatMessageDtoToChatMessage(ChatMessageDto chatMessageDto);
}

