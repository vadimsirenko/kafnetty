package org.kafnetty.service;

import org.kafnetty.dto.MessageDto;
import org.kafnetty.dto.MessageListDto;

import java.util.List;

public interface MessageService {
    MessageDto processMessage(MessageDto message);

    MessageListDto processMessageList(MessageListDto message);

    void setMessageAsSent(MessageDto message);

    List<MessageDto> getNotSyncMessages();
}
