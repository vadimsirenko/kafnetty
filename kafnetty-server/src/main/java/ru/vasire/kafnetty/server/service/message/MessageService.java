package ru.vasire.kafnetty.server.service.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vasire.kafnetty.server.mapper.ChatMessageMapper;
import ru.vasire.kafnetty.server.repository.ChatMessageRepository;
import ru.vasire.kafnetty.server.dto.ChatMessageDto;
import ru.vasire.kafnetty.server.dto.MessageListDto;
import ru.vasire.kafnetty.server.entity.ChatMessage;

@Service
@RequiredArgsConstructor
public final class MessageService {
    private final ChatMessageRepository chatMessageRepository;
    //private final RoomChannelRepository roomChannelRepository;
    //private final ClientService clientService;

    public static ChatMessage messageEncode(String requestJson) {
        try {
            ChatMessageDto chatMessageDto = new ObjectMapper().readValue(requestJson, ChatMessageDto.class);
            if (!validateRequest(chatMessageDto))
                throw new RuntimeException("ChatMessage is not valid");
            ChatMessage chatMessage = ChatMessageMapper.INSTANCE.ChatMessageDtoToChatMessage(chatMessageDto);
            return chatMessage;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public ChatMessageDto processRequest(String req) throws JsonProcessingException {
        ChatMessage chatMessage = MessageService.messageEncode(req);
        if (!chatMessageRepository.existsById(chatMessage.getId())) {
            chatMessageRepository.saveAndFlush(chatMessage);
        }
        return ChatMessageMapper.INSTANCE.ChatMessageToChatMessageDto(chatMessage);
    }

    public MessageListDto processMessageListRequest(String req) throws JsonProcessingException {
        MessageListDto chatMessageListDto = new ObjectMapper().readValue(req, MessageListDto.class);
        if (chatMessageListDto.getRoomId() == null) {
            // TODO обработать неверный вход
            throw new RuntimeException("Illegal value roomId");
        }
        chatMessageListDto.setMessages(
                chatMessageRepository.findByRoomId(chatMessageListDto.getRoomId()).stream().map(ChatMessageMapper.INSTANCE::ChatMessageToChatMessageDto).toList());
        return chatMessageListDto;
    }

    /**
     * Check that message structure is valid
     *
     * @param chatMessageDto
     * @return
     */
    public static boolean validateRequest(ChatMessageDto chatMessageDto) {
        return true;
    }
}
