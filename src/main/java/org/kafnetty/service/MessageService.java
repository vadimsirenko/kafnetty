package org.kafnetty.service;

import io.netty.channel.Channel;
import org.kafnetty.dto.channel.ChannelBaseDto;
import org.kafnetty.dto.channel.ChannelMessageDto;
import org.kafnetty.dto.channel.ChannelMessageListDto;
import org.kafnetty.mapper.MessageMapperImpl;

import java.util.UUID;

public interface MessageService {
    ChannelMessageDto processMessage(ChannelMessageDto message, Channel channel);

    ChannelMessageListDto processMessageList(ChannelMessageListDto message, Channel channel);

    ChannelMessageListDto getMessageListByRoomId(UUID roomId, UUID senderId);
    void setMessageAsSended(ChannelMessageDto message);
}
