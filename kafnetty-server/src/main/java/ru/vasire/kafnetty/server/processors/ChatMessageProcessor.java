package ru.vasire.kafnetty.server.processors;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.vasire.kafnetty.server.dto.BaseDto;
import ru.vasire.kafnetty.server.dto.ChatMessageDto;
import ru.vasire.kafnetty.server.dto.MessageListDto;
import ru.vasire.kafnetty.server.entity.ChatMessage;
import ru.vasire.kafnetty.server.mapper.ChatMessageMapper;
import ru.vasire.kafnetty.server.repository.ChatMessageRepository;

@Component
@RequiredArgsConstructor
public final class ChatMessageProcessor {
    private final ChatMessageRepository chatMessageRepository;
    public ChatMessageDto processMessage(BaseDto message, Channel channel) {
        ChatMessage chatMessage = ChatMessageMapper.INSTANCE.ChatMessageDtoToChatMessage((ChatMessageDto)message);
        if (!chatMessageRepository.existsById(chatMessage.getId())) {
            chatMessageRepository.saveAndFlush(chatMessage);
        }
        return ChatMessageMapper.INSTANCE.ChatMessageToChatMessageDto(chatMessage);
    }
    public MessageListDto processMessageList(BaseDto message, Channel channel) {
        MessageListDto messageListDto = (MessageListDto)message;
        if (messageListDto.getRoomId() == null) {
            // TODO обработать неверный вход
            throw new RuntimeException("Illegal value roomId");
        }
        messageListDto.setMessages(chatMessageRepository.findByRoomId(messageListDto.getRoomId()).stream().map(ChatMessageMapper.INSTANCE::ChatMessageToChatMessageDto).toList());
        return messageListDto;
    }
}
