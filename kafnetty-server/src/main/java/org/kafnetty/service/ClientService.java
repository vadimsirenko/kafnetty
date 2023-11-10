package org.kafnetty.service;

import io.netty.channel.Channel;
import org.kafnetty.dto.ClientDto;

import java.util.List;
import java.util.UUID;

public interface ClientService {
    ClientDto getChannelUser(String channelLongId);

    boolean existsChannelUser(String channelLongId);

    void setRoomForChannelUser(UUID roomId, String channelLongId);

    void removeChannelUser(String channelLongId);

    ClientDto processMessage(ClientDto message, Channel channel);

    void setClientAsSent(ClientDto channelClientDto);

    List<ClientDto> getNotSyncClients();
}


