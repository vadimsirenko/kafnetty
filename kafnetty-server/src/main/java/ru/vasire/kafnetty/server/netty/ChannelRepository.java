package ru.vasire.kafnetty.server.netty;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.vasire.kafnetty.server.dto.InfoDto;
import ru.vasire.kafnetty.server.dto.UserProfileDto;
import ru.vasire.kafnetty.server.service.message.ClientService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class ChannelRepository {
    private final ClientService clientService;
    private final Map<UUID, ChannelGroup> CHANNEL_GROUP_MAP = new ConcurrentHashMap<>();

    public void put(UUID roomId, Channel channel) {
        if (!CHANNEL_GROUP_MAP.containsKey(roomId)) {
            CHANNEL_GROUP_MAP.put(roomId, new DefaultChannelGroup(GlobalEventExecutor.INSTANCE));
        }
        UserProfileDto userProfileDto = clientService.getProfile(channel.id().asLongText());
        UUID roomIdOld = (userProfileDto != null)? userProfileDto.getRoomId(): null;
        if(roomIdOld != null && roomIdOld != roomId && CHANNEL_GROUP_MAP.get(roomIdOld).contains(channel))
        {
            CHANNEL_GROUP_MAP.get(roomIdOld).remove(channel);
            CHANNEL_GROUP_MAP.get(roomIdOld).writeAndFlush(InfoDto.createLogoffInfo(userProfileDto.getNickName()).toWebSocketFrame());
        }
        if(!CHANNEL_GROUP_MAP.get(roomId).contains(channel)) {
            CHANNEL_GROUP_MAP.get(roomId).add(channel);
            CHANNEL_GROUP_MAP.get(roomId).writeAndFlush(InfoDto.createLogonInfo(userProfileDto.getNickName()).toWebSocketFrame());
            clientService.setRoomForUserProfile(roomId, channel.id().asLongText());
        }
    }
    public ChannelGroup getRoomChannels(UUID roomId){
        return CHANNEL_GROUP_MAP.get(roomId);
    }

    public void remove(Channel channel){
        UserProfileDto userProfileDto = clientService.getProfile(channel.id().asLongText());
        UUID roomIdOld = (userProfileDto != null)? userProfileDto.getRoomId(): null;
        if(roomIdOld != null && CHANNEL_GROUP_MAP.get(roomIdOld).contains(channel)) {
            CHANNEL_GROUP_MAP.get(roomIdOld).remove(channel);
        }
        CHANNEL_GROUP_MAP.get(roomIdOld).writeAndFlush(InfoDto.createLogoffInfo(userProfileDto.getNickName()).toWebSocketFrame());
        clientService.removeProfile(channel.id().asLongText());
    }
}