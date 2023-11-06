package org.kafnetty.repository;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import org.kafnetty.dto.channel.ChannelBaseDto;

import java.util.UUID;

public interface ChannelRepository {
    interface SingleGroupEventCallback {
        void run(ChannelGroup group);
    }

    void changeRoom(UUID roomId, UUID roomIdOld, Channel channel, SingleGroupEventCallback removeCallback, SingleGroupEventCallback addCallback);

    void removeChannelFromRoom(UUID roomId, Channel channel, SingleGroupEventCallback removeCallback);

    void sendToRoom(UUID roomId, ChannelBaseDto channelRoomDto);

    void sendToAllRoom(ChannelBaseDto channelRoomDto);

}
