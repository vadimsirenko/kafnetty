package ru.vasire.kafnetty.server.service.message;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vasire.kafnetty.server.dto.ChatMessageDto;
import ru.vasire.kafnetty.server.dto.MessageListDto;
import ru.vasire.kafnetty.server.entity.ChatMessage;
import ru.vasire.kafnetty.server.mapper.ChatMessageMapper;
import ru.vasire.kafnetty.server.repository.ChatMessageRepository;

@Service
@RequiredArgsConstructor
public final class ChatMessageService{
    private final ChatMessageRepository chatMessageRepository;

    public static ChatMessage messageEncode(String requestJson) {
        ChatMessageDto chatMessageDto = ChatMessageDto.encode(requestJson, ChatMessageDto.class);
        if (!validateRequest(chatMessageDto))
            throw new RuntimeException("ChatMessage is not valid");
        return ChatMessageMapper.INSTANCE.ChatMessageDtoToChatMessage(chatMessageDto);
    }

    public ChatMessageDto processRequest(String req) {
        ChatMessage chatMessage = ChatMessageService.messageEncode(req);
        if (!chatMessageRepository.existsById(chatMessage.getId())) {
            chatMessageRepository.saveAndFlush(chatMessage);
        }
        return ChatMessageMapper.INSTANCE.ChatMessageToChatMessageDto(chatMessage);
    }

    public MessageListDto processMessageListRequest(String req) {
        MessageListDto chatMessageListDto = MessageListDto.encode(req, MessageListDto.class);
        if (chatMessageListDto.getRoomId() == null) {
            // TODO обработать неверный вход
            throw new RuntimeException("Illegal value roomId");
        }
        chatMessageListDto.setMessages(chatMessageRepository.findByRoomId(chatMessageListDto.getRoomId()).stream().map(ChatMessageMapper.INSTANCE::ChatMessageToChatMessageDto).toList());
        return chatMessageListDto;
    }

    public static boolean validateRequest(ChatMessageDto chatMessageDto) {
        return true;
    }
}
