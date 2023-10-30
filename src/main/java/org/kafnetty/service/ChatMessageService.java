package org.kafnetty.service;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import org.kafnetty.dto.channel.ChannelBaseDto;
import org.kafnetty.dto.channel.ChannelMessageDto;
import org.kafnetty.dto.channel.ChannelMessageListDto;
import org.kafnetty.entity.Message;
import org.kafnetty.mapper.MessageMapper;
import org.kafnetty.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public final class ChatMessageService {
    @Autowired
    private final MessageMapper messageMapper;

    private final MessageRepository messageRepository;

    public ChannelMessageDto processMessage(ChannelBaseDto message, Channel channel) {
        Message chatMessage = messageMapper.ChannelMessageDtoToChatMessage((ChannelMessageDto) message);
        if (!messageRepository.existsById(chatMessage.getId())) {
            messageRepository.saveAndFlush(chatMessage);
        }
        return messageMapper.MessageToChannelMessageDto(chatMessage);
    }

    public ChannelMessageListDto processMessageList(ChannelBaseDto message, Channel channel) {
        ChannelMessageListDto messageListDto = (ChannelMessageListDto) message;
        return getMessageListByRoomId(messageListDto.getRoomId(), messageListDto.getSenderId());
    }

    public ChannelMessageListDto getMessageListByRoomId(UUID roomId, UUID senderId) {
        ChannelMessageListDto messageListDto = new ChannelMessageListDto(roomId, senderId);
        if (messageListDto.getRoomId() == null) {
            // TODO обработать неверный вход
            throw new RuntimeException("Illegal value roomId");
        }
        messageListDto.setMessages(messageRepository.findByRoomId(roomId).stream().map(messageMapper::MessageToChannelMessageDto).toList());
        return messageListDto;
    }
}
