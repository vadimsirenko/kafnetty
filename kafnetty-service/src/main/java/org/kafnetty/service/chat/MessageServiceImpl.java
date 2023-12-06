package org.kafnetty.service.chat;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kafnetty.dto.BaseDto;
import org.kafnetty.dto.MessageDto;
import org.kafnetty.dto.MessageListDto;
import org.kafnetty.dto.UserDto;
import org.kafnetty.session.UserContext;
import org.kafnetty.store.entity.Message;
import org.kafnetty.store.entity.User;
import org.kafnetty.mapper.MessageMapper;
import org.kafnetty.store.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public final class MessageServiceImpl implements MessageService {
    private final MessageMapper messageMapper;
    private final MessageRepository messageRepository;

    @Override
    public MessageDto processMessage(MessageDto message, String groupId) {
        Message chatMessage = messageMapper.MessageDtoToMessage(message);
        if (!messageRepository.existsById(chatMessage.getId())) {
            chatMessage.setSent(!groupId.equals(message.getClusterId()));
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
    public void setMessageAsSent(MessageDto channelMessageDto) {
        Optional<Message> messageOptional = messageRepository.findById(channelMessageDto.getId());
        if (messageOptional.isPresent()) {
            Message message = messageOptional.get();
            message.setSent(true);
            messageRepository.saveAndFlush(message);
        }
    }

    @Override
    public List<MessageDto> getNotSyncMessages(String groupId) {
        List<Message> messages = messageRepository.findBySentAndClusterId(false, groupId);
        return messageMapper.ToMessageDtoList(messages);
    }

    @Override
    public MessageDto createMessage(BaseDto message, Channel channel) {
        UserDto user = UserContext.getContext(channel).getUser();
        MessageDto receiveMessage = (MessageDto)message;
        receiveMessage.setSenderId(user.getId());
        receiveMessage.setSender(user.getNickName());
        return receiveMessage;
    }
}
