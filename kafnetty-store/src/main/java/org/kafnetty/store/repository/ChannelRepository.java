package org.kafnetty.store.repository;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import org.kafnetty.dto.BaseDto;

import java.util.UUID;

public interface ChannelRepository {
    void changeRoom(UUID roomId, UUID roomIdOld, Channel channel, SingleGroupEventCallback removeCallback, SingleGroupEventCallback addCallback);

    void removeChannelFromRoom(UUID roomId, Channel channel, SingleGroupEventCallback removeCallback);

    void sendToRoom(UUID roomId, BaseDto channelRoomDto);

    void sendToAllRoom(BaseDto channelRoomDto);

    interface SingleGroupEventCallback {
        void run(ChannelGroup group);
    }

}
