package org.kafnetty.service;

import io.netty.channel.Channel;
import org.kafnetty.dto.channel.ChannelClientDto;
import org.kafnetty.dto.channel.ChannelMessageDto;
import org.kafnetty.dto.channel.ChannelMessageListDto;
import org.kafnetty.entity.Client;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    ChannelMessageDto processLocalMessage(ChannelMessageDto message, Channel channel);
    ChannelMessageDto processMessage(ChannelMessageDto message);
    ChannelMessageListDto processMessageList(ChannelMessageListDto message, Channel channel);
    ChannelMessageListDto getMessageListByRoomId(UUID roomId, UUID senderId);
    void setMessageAsSended(ChannelMessageDto message);
    List<ChannelMessageDto> getNotSyncMessages();
}
