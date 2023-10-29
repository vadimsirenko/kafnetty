package ru.vasire.kafnetty.server.processors;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.vasire.kafnetty.dto.*;
import ru.vasire.kafnetty.mapper.UserProfileDtoMapper;
import ru.vasire.kafnetty.server.service.KafkaProducerService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class ChatProcessor {
    @Autowired
    private UserProfileDtoMapper userProfileDtoMapper;

    private final ClientProcessor clientProcessor;
    private final ChatMessageProcessor chatMessageProcessor;
    private final RoomProcessor roomProcessor;
    private final KafkaProducerService kafkaProducerService;
    private final Map<UUID, ChannelGroup> CHANNEL_GROUP_MAP = new ConcurrentHashMap<>();

    public void putChannel(UUID roomId, Channel channel) {
        if (!CHANNEL_GROUP_MAP.containsKey(roomId)) {
            CHANNEL_GROUP_MAP.put(roomId, new DefaultChannelGroup(GlobalEventExecutor.INSTANCE));
        }
        UserProfileDto userProfileDto = clientProcessor.getProfile(channel.id().asLongText());
        UUID roomIdOld = (userProfileDto != null)? userProfileDto.getRoomId(): null;
        if(roomIdOld != null && !roomIdOld.equals(roomId) && CHANNEL_GROUP_MAP.get(roomIdOld).contains(channel))
        {
            CHANNEL_GROUP_MAP.get(roomIdOld).remove(channel);
            CHANNEL_GROUP_MAP.get(roomIdOld).writeAndFlush(new TextWebSocketFrame(InfoDto.createLogoffInfo(userProfileDto.getNickName()).toJson()));
        }
        if(!CHANNEL_GROUP_MAP.get(roomId).contains(channel)) {
            CHANNEL_GROUP_MAP.get(roomId).add(channel);
            CHANNEL_GROUP_MAP.get(roomId).writeAndFlush(new TextWebSocketFrame(InfoDto.createLogonInfo(userProfileDto.getNickName()).toJson()));
            clientProcessor.setRoomForUserProfile(roomId, channel.id().asLongText());
        }
    }
    public ChannelGroup getRoomChannels(UUID roomId){
        return CHANNEL_GROUP_MAP.get(roomId);
    }

    public void removeChannel(Channel channel){
        UserProfileDto userProfileDto = clientProcessor.getProfile(channel.id().asLongText());
        UUID roomIdOld = (userProfileDto != null)? userProfileDto.getRoomId(): null;
        if(roomIdOld != null && CHANNEL_GROUP_MAP.get(roomIdOld).contains(channel)) {
            CHANNEL_GROUP_MAP.get(roomIdOld).remove(channel);
        }
        assert userProfileDto != null;
        CHANNEL_GROUP_MAP.get(roomIdOld).writeAndFlush(new TextWebSocketFrame(InfoDto.createLogoffInfo(userProfileDto.getNickName()).toJson()));
        clientProcessor.removeProfile(channel.id().asLongText());
    }
    public void processMessage(String jsonMessage, Channel channel) {
        BaseDto messageDto = BaseDto.decode(jsonMessage);
        switch (messageDto.getMessageType()) {
            case MESSAGE:
                ChatMessageDto chatMessageDto = chatMessageProcessor.processMessage(messageDto, channel);
                getRoomChannels(clientProcessor.getProfile(channel.id().asLongText()).getRoomId()).writeAndFlush(new TextWebSocketFrame(chatMessageDto.toJson()));
                kafkaProducerService.sendMessage(chatMessageDto);
                break;
            case ROOM:
                RoomDto roomDto = roomProcessor.processMessage(messageDto, channel);
                getRoomChannels(clientProcessor.getProfile(channel.id().asLongText()).getRoomId()).writeAndFlush(new TextWebSocketFrame(roomDto.toJson()));
                kafkaProducerService.sendRoom(roomDto);
                break;
            case MESSAGE_LIST:
                MessageListDto ml = chatMessageProcessor.processMessageList(messageDto, channel);
                putChannel(ml.getRoomId(), channel);
                channel.writeAndFlush(new TextWebSocketFrame(ml.toJson()));
                break;
            case CLIENT:
                ClientDto clientDto = clientProcessor.processMessage(messageDto, channel);
                putChannel(clientDto.getRoomId(), channel);
                break;
            default:
        }
    }

    public boolean existsUserProfile(Channel channel){
        return clientProcessor.existsUserProfile(channel.id().asLongText());
    }

    public void InitChannel(Channel channel) {
        UserProfileDto userProfileDto = clientProcessor.getProfile(channel.id().asLongText());
        if (userProfileDto.getId() == null) {
            System.out.println(channel + " tourist");
        } else {
            channel.writeAndFlush(new TextWebSocketFrame(roomProcessor.getRoomList(userProfileDto.getId()).toJson()));
            channel.writeAndFlush(new TextWebSocketFrame(userProfileDtoMapper.UserProfileDtoToClientDto(userProfileDto).toJson()));
        }
    }
}
