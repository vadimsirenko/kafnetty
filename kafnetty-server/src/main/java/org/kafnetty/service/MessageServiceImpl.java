package org.kafnetty.service;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kafnetty.dto.channel.ChannelMessageDto;
import org.kafnetty.dto.channel.ChannelMessageListDto;
import org.kafnetty.entity.Client;
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
    public ChannelMessageDto processLocalMessage(ChannelMessageDto message, Channel channel) {
        message.setClusterId(kafnettyKafkaConfig.getGroupId());
        return processMessage(message);
    }

    @Override
    public ChannelMessageDto processMessage(ChannelMessageDto message) {
        Message chatMessage = messageMapper.ChannelMessageDtoToMessage(message);
        if (!messageRepository.existsById(chatMessage.getId())) {
            chatMessage.setSent(!kafnettyKafkaConfig.getGroupId().equals(message.getClusterId()));
            messageRepository.saveAndFlush(chatMessage);
        }
        return messageMapper.MessageToChannelMessageDto(chatMessage);
    }

    @Override
    public ChannelMessageListDto processMessageList(ChannelMessageListDto messageListDto, Channel channel) {
        return getMessageListByRoomId(messageListDto.getRoomId(), messageListDto.getSenderId());
    }

    @Override
    public ChannelMessageListDto getMessageListByRoomId(UUID roomId, UUID senderId) {
        ChannelMessageListDto messageListDto = new ChannelMessageListDto(roomId, senderId);
        if (messageListDto.getRoomId() == null) {
            // TODO обработать неверный вход
            throw new RuntimeException("Illegal value roomId");
        }
        messageListDto.setMessages(messageRepository.findByRoomIdOrderByTs(roomId).stream().map(messageMapper::MessageToChannelMessageDto).toList());
        return messageListDto;
    }

    @Override
    public void setMessageAsSended(ChannelMessageDto channelMessageDto) {
        Optional<Message> messageOptional = messageRepository.findById(channelMessageDto.getId());
        if (messageOptional.isPresent()) {
            Message message = messageOptional.get();
            message.setSent(true);
            messageRepository.saveAndFlush(message);
        }
    }

    @Override
    public List<ChannelMessageDto> getNotSyncMessages() {
        List<Message> messages = messageRepository.findAllByIsSentAndClusterId(false, kafnettyKafkaConfig.getGroupId());
        return messageMapper.mapToChannelMessageDtoList(messages);
    }
}
