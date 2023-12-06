package org.kafnetty.service.chat;

import io.netty.channel.Channel;
import org.kafnetty.dto.BaseDto;
import org.kafnetty.dto.MessageDto;
import org.kafnetty.dto.MessageListDto;

import java.util.List;

public interface MessageService {
    MessageDto processMessage(MessageDto message, String groupId);

    MessageListDto processMessageList(MessageListDto message);

    void setMessageAsSent(MessageDto message);

    List<MessageDto> getNotSyncMessages(String groupId);

    MessageDto createMessage(BaseDto message, Channel channel);
}
