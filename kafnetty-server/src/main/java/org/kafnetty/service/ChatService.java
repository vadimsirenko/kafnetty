package org.kafnetty.service;

import io.netty.channel.Channel;

public interface ChatService {

    void removeChannel(Channel channel);

    void processMessage(String jsonMessage, Channel channel);

    boolean existsChannelUser(Channel channel);

    void InitChannel(Channel channel);
}
