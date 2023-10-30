package org.kafnetty.repository;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;

import java.util.UUID;

public interface ChannelRepository {
    interface RoomEventCallback {
        void run(ChannelGroup group);
    }
    void changeRoom(UUID roomId, UUID roomIdOld, Channel channel, RoomEventCallback removeCallback, RoomEventCallback addCallback);

    void removeChannelFromRoom(UUID roomId, Channel channel, RoomEventCallback removeCallback);

    void applyToRoom(UUID roomId, RoomEventCallback applyCallback);
}
