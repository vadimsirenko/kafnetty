package ru.vasire.kafnetty.server.processors;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.vasire.kafnetty.server.dto.*;
import ru.vasire.kafnetty.server.mapper.UserProfileDtoMapper;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public final class ChatProcessor {
    private final ClientPoocessor clientPoocessor;
    private final ChatMessageProcessor chatMessageProcessor;
    private final RoomProcessor roomProcessor;
    private final Map<UUID, ChannelGroup> CHANNEL_GROUP_MAP = new ConcurrentHashMap<>();

    public void putChannel(UUID roomId, Channel channel) {
        if (!CHANNEL_GROUP_MAP.containsKey(roomId)) {
            CHANNEL_GROUP_MAP.put(roomId, new DefaultChannelGroup(GlobalEventExecutor.INSTANCE));
        }
        UserProfileDto userProfileDto = clientPoocessor.getProfile(channel.id().asLongText());
        UUID roomIdOld = (userProfileDto != null)? userProfileDto.getRoomId(): null;
        if(roomIdOld != null && roomIdOld != roomId && CHANNEL_GROUP_MAP.get(roomIdOld).contains(channel))
        {
            CHANNEL_GROUP_MAP.get(roomIdOld).remove(channel);
            CHANNEL_GROUP_MAP.get(roomIdOld).writeAndFlush(InfoDto.createLogoffInfo(userProfileDto.getNickName()).toWebSocketFrame());
        }
        if(!CHANNEL_GROUP_MAP.get(roomId).contains(channel)) {
            CHANNEL_GROUP_MAP.get(roomId).add(channel);
            CHANNEL_GROUP_MAP.get(roomId).writeAndFlush(InfoDto.createLogonInfo(userProfileDto.getNickName()).toWebSocketFrame());
            clientPoocessor.setRoomForUserProfile(roomId, channel.id().asLongText());
        }
    }
    public ChannelGroup getRoomChannels(UUID roomId){
        return CHANNEL_GROUP_MAP.get(roomId);
    }

    public void removeChannel(Channel channel){
        UserProfileDto userProfileDto = clientPoocessor.getProfile(channel.id().asLongText());
        UUID roomIdOld = (userProfileDto != null)? userProfileDto.getRoomId(): null;
        if(roomIdOld != null && CHANNEL_GROUP_MAP.get(roomIdOld).contains(channel)) {
            CHANNEL_GROUP_MAP.get(roomIdOld).remove(channel);
        }
        CHANNEL_GROUP_MAP.get(roomIdOld).writeAndFlush(InfoDto.createLogoffInfo(userProfileDto.getNickName()).toWebSocketFrame());
        clientPoocessor.removeProfile(channel.id().asLongText());
    }
    public void processMessage(String jsonMessage, Channel channel) {
        BaseDto messageDto = BaseDto.decode(jsonMessage);
        switch (messageDto.getMessageType()) {
            case MESSAGE:
                ChatMessageDto m = chatMessageProcessor.processMessage(messageDto, channel);
                getRoomChannels(clientPoocessor.getProfile(channel.id().asLongText()).getRoomId()).writeAndFlush(m.toWebSocketFrame());
                break;
            case ROOM:
                RoomDto r = roomProcessor.processMessage(messageDto, channel);
                getRoomChannels(clientPoocessor.getProfile(channel.id().asLongText()).getRoomId()).writeAndFlush(r.toWebSocketFrame());
                break;
            case MESSAGE_LIST:
                MessageListDto ml = chatMessageProcessor.processMessageList(messageDto, channel);
                putChannel(ml.getRoomId(), channel);
                channel.writeAndFlush(ml.toWebSocketFrame());
                break;
            case CLIENT:
                ClientDto clientDto = clientPoocessor.processMessage(messageDto, channel);
                putChannel(clientDto.getRoomId(), channel);
                break;
            default:
        }
    }

    public boolean existsUserProfile(Channel channel){
        return clientPoocessor.existsUserProfile(channel.id().asLongText());
    }

    public void InitChannel(Channel channel) {
        UserProfileDto userProfileDto = clientPoocessor.getProfile(channel.id().asLongText());
        if (userProfileDto.getId() == null) {
            System.out.println(channel + " tourist");
        } else {
            channel.writeAndFlush(roomProcessor.getRoomList(userProfileDto.getId()).toWebSocketFrame());
            channel.writeAndFlush(UserProfileDtoMapper.INSTANCE.UserProfileDtoToClientDto(userProfileDto).toWebSocketFrame());
        }
    }
}
