package ru.vasire.kafnetty.server.processors;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.vasire.kafnetty.dto.BaseDto;
import ru.vasire.kafnetty.dto.ChatMessageDto;
import ru.vasire.kafnetty.dto.MessageListDto;
import ru.vasire.kafnetty.entity.ChatMessage;
import ru.vasire.kafnetty.mapper.ChatMessageMapper;
import ru.vasire.kafnetty.server.repository.ChatMessageRepository;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public final class ChatMessageProcessor {
    @Autowired
    private ChatMessageMapper chatMessageMapper;

    private final ChatMessageRepository chatMessageRepository;
    public ChatMessageDto processMessage(BaseDto message, Channel channel) {
        ChatMessage chatMessage = chatMessageMapper.ChatMessageDtoToChatMessage((ChatMessageDto)message);
        if (!chatMessageRepository.existsById(chatMessage.getId())) {
            chatMessageRepository.saveAndFlush(chatMessage);
        }
        return chatMessageMapper.ChatMessageToChatMessageDto(chatMessage);
    }
    public MessageListDto processMessageList(BaseDto message, Channel channel) {
        MessageListDto messageListDto = (MessageListDto)message;
        return getMessageListByRoomId(messageListDto.getRoomId(), messageListDto.getSenderId());
    }

    public MessageListDto getMessageListByRoomId(UUID roomId, UUID senderId) {
        MessageListDto messageListDto = new MessageListDto(roomId, senderId);
        if (messageListDto.getRoomId() == null) {
            // TODO обработать неверный вход
            throw new RuntimeException("Illegal value roomId");
        }
        messageListDto.setMessages(chatMessageRepository.findByRoomId(roomId).stream().map(chatMessageMapper::ChatMessageToChatMessageDto).toList());
        return messageListDto;
    }
}
