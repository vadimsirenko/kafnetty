package org.kafnetty.service;

import io.netty.channel.Channel;
import org.kafnetty.dto.UserProfileDto;
import org.kafnetty.dto.channel.ChannelBaseDto;
import org.kafnetty.dto.channel.ChannelClientDto;

import java.util.UUID;

public interface ClientService {
    UserProfileDto getProfile(String channelLongId);

    boolean existsUserProfile(String channelLongId);

    void setRoomForUserProfile(UUID roomId, String channelLongId);

    void removeProfile(String channelLongId);

    ChannelClientDto processMessage(ChannelBaseDto message, Channel channel);
}


