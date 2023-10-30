package org.kafnetty.repository;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ChannelRepositoryImpl implements ChannelRepository{
    private final Map<UUID, ChannelGroup> CHANNEL_GROUP_MAP = new ConcurrentHashMap<>();

    private void makeSureRoom(UUID roomId) {
        if (!CHANNEL_GROUP_MAP.containsKey(roomId)) {
            CHANNEL_GROUP_MAP.put(roomId, new DefaultChannelGroup(GlobalEventExecutor.INSTANCE));
        }
    }

    @Override
    public void changeRoom(UUID roomId, UUID roomIdOld, Channel channel, RoomEventCallback removeCallback, RoomEventCallback addCallback){
        makeSureRoom(roomId);
        if (roomIdOld != null && !roomIdOld.equals(roomId) && CHANNEL_GROUP_MAP.get(roomIdOld).contains(channel)) {
            CHANNEL_GROUP_MAP.get(roomIdOld).remove(channel);
            removeCallback.run(CHANNEL_GROUP_MAP.get(roomIdOld));
        }
        if (!CHANNEL_GROUP_MAP.get(roomId).contains(channel)) {
            CHANNEL_GROUP_MAP.get(roomId).add(channel);
            addCallback.run(CHANNEL_GROUP_MAP.get(roomId));
        }
    }
    @Override
    public void removeChannelFromRoom(UUID roomId, Channel channel, RoomEventCallback removeCallback){
        if (roomId != null && CHANNEL_GROUP_MAP.containsKey(roomId) && CHANNEL_GROUP_MAP.get(roomId).contains(channel)) {
            CHANNEL_GROUP_MAP.get(roomId).remove(channel);
            removeCallback.run(CHANNEL_GROUP_MAP.get(roomId));
        }
    }
    @Override
    public void applyToRoom(UUID roomId, RoomEventCallback applyCallback){
        if (roomId != null && CHANNEL_GROUP_MAP.containsKey(roomId)) {
            applyCallback.run(CHANNEL_GROUP_MAP.get(roomId));
        }
    }
}
