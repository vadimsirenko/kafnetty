package org.kafnetty.service;

import io.netty.channel.Channel;
import org.kafnetty.dto.MessageDto;
import org.kafnetty.dto.MessageListDto;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageDto processMessage(MessageDto message);

    MessageListDto processMessageList(MessageListDto message, Channel channel);

    MessageListDto getMessageListByRoomId(UUID roomId, UUID senderId);

    void setMessageAsSended(MessageDto message);

    List<MessageDto> getNotSyncMessages();
}
