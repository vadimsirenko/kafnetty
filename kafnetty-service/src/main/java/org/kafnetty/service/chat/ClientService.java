package org.kafnetty.service.chat;

import io.netty.channel.Channel;
import org.kafnetty.dto.UserDto;

import java.util.List;
import java.util.UUID;

public interface ClientService {
    UserDto getChannelUser(String channelLongId);

    boolean existsChannelUser(String channelLongId);

    void setRoomForChannelUser(UUID roomId, String channelLongId);

    void removeChannelUser(String channelLongId);

    UserDto processMessage(UserDto message, Channel channel, String groupId);

    void setClientAsSent(UserDto channelUserDto);

    List<UserDto> getNotSyncClients(String groupId);
}


