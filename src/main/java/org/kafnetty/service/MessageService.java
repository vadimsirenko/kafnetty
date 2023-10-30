package org.kafnetty.service;

import io.netty.channel.Channel;
import org.kafnetty.dto.channel.ChannelBaseDto;
import org.kafnetty.dto.channel.ChannelMessageDto;
import org.kafnetty.dto.channel.ChannelMessageListDto;

import java.util.UUID;

public interface MessageService {
    ChannelMessageDto processMessage(ChannelBaseDto message, Channel channel);

    ChannelMessageListDto processMessageList(ChannelBaseDto message, Channel channel);

    ChannelMessageListDto getMessageListByRoomId(UUID roomId, UUID senderId);
}
