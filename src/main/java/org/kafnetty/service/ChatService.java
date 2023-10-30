package org.kafnetty.service;

import io.netty.channel.Channel;

import java.util.UUID;

public interface ChatService {
    void putChannel(UUID roomId, Channel channel);

    void removeChannel(Channel channel);

    void processMessage(String jsonMessage, Channel channel);

    boolean existsUserProfile(Channel channel);

    void InitChannel(Channel channel);
}
