package org.kafnetty.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kafnetty.dto.MessageDto;
import org.kafnetty.dto.MessageListDto;
import org.kafnetty.entity.Message;
import org.kafnetty.kafka.config.KafnettyKafkaConfig;
import org.kafnetty.mapper.MessageMapper;
import org.kafnetty.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public final class MessageServiceImpl implements MessageService {
    private final KafnettyKafkaConfig kafnettyKafkaConfig;
    private final MessageMapper messageMapper;
    private final MessageRepository messageRepository;

    @Override
    public MessageDto processMessage(MessageDto message) {
        Message chatMessage = messageMapper.MessageDtoToMessage(message);
        if (!messageRepository.existsById(chatMessage.getId())) {
            chatMessage.setSent(!kafnettyKafkaConfig.getGroupId().equals(message.getClusterId()));
            messageRepository.saveAndFlush(chatMessage);
        }
        return messageMapper.MessageToChannelMessageDto(chatMessage);
    }

    @Override
    public MessageListDto processMessageList(MessageListDto messageListDto) {
        return getMessageListByRoomId(messageListDto.getRoomId(), messageListDto.getSenderId());
    }
    private MessageListDto getMessageListByRoomId(UUID roomId, UUID senderId) {
        MessageListDto messageListDto = new MessageListDto(roomId, senderId);
        if (messageListDto.getRoomId() == null) {
            // TODO обработать неверный вход
            throw new RuntimeException("Illegal value roomId");
        }
        messageListDto.setMessages(messageRepository.findByRoomIdOrderByTs(roomId).stream().map(messageMapper::MessageToChannelMessageDto).toList());
        return messageListDto;
    }

    @Override
    public void setMessageAsSended(MessageDto channelMessageDto) {
        Optional<Message> messageOptional = messageRepository.findById(channelMessageDto.getId());
        if (messageOptional.isPresent()) {
            Message message = messageOptional.get();
            message.setSent(true);
            messageRepository.saveAndFlush(message);
        }
    }

    @Override
    public List<MessageDto> getNotSyncMessages() {
        List<Message> messages = messageRepository.findAllByIsSentAndClusterId(false, kafnettyKafkaConfig.getGroupId());
        return messageMapper.ToMessageDtoList(messages);
    }
}
