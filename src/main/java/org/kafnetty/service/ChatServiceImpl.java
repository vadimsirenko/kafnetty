package org.kafnetty.service;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import org.kafnetty.dto.UserProfileDto;
import org.kafnetty.dto.channel.*;
import org.kafnetty.mapper.MessageMapper;
import org.kafnetty.mapper.UserProfileDtoMapper;
import org.kafnetty.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{
    private final ClientService clientService;
    private final MessageService messageService;
    private final RoomService roomService;
    @Autowired
    @Qualifier("kafkaProducerService")
    private KafkaProducerService kafkaProducerService;
    private final ChannelRepository channelRepository;

    //private final Map<UUID, ChannelGroup> CHANNEL_GROUP_MAP = new ConcurrentHashMap<>();
    @Autowired
    private UserProfileDtoMapper userProfileDtoMapper;
    @Autowired
    private MessageMapper messageMapper;

    @Override
    public void putChannel(UUID roomId, Channel channel) {
        UserProfileDto userProfileDto = clientService.getProfile(channel.id().asLongText());
        UUID oldRoomId = (userProfileDto != null) ? userProfileDto.getRoomId() : null;
        channelRepository.changeRoom(roomId, oldRoomId, channel
                , oldGroup -> {
                    assert userProfileDto != null;
                    ChannelInfoDto.createLogoffInfo(userProfileDto.getNickName()).writeAndFlush(oldGroup);}
                , group -> {
                    assert userProfileDto != null;
                    ChannelInfoDto.createLogonInfo(userProfileDto.getNickName()).writeAndFlush(group);
                    clientService.setRoomForUserProfile(roomId, channel.id().asLongText());
                });
    }
    @Override
    public void removeChannel(Channel channel) {
        UserProfileDto userProfileDto = clientService.getProfile(channel.id().asLongText());
        UUID roomIdOld = (userProfileDto != null) ? userProfileDto.getRoomId() : null;
        assert userProfileDto != null;
        channelRepository.removeChannelFromRoom(roomIdOld, channel,
                oldGroup-> ChannelInfoDto.createLogoffInfo(userProfileDto.getNickName()).writeAndFlush(oldGroup));
        clientService.removeProfile(channel.id().asLongText());
    }
    @Override
    public void processMessage(String jsonMessage, Channel channel) {
        ChannelBaseDto messageDto = ChannelBaseDto.decode(jsonMessage);
        switch (messageDto.getMessageType()) {
            case MESSAGE:
                ChannelMessageDto channelMessageDto = messageService.processMessage(messageDto, channel);
                channelRepository.applyToRoom(clientService.getProfile(channel.id().asLongText()).getRoomId(),
                        channelMessageDto::writeAndFlush);
                kafkaProducerService.sendMessage(messageMapper.ChannelMessageDtoToKafkaMessageDto(channelMessageDto));
                break;
            case ROOM:
                ChannelRoomDto channelRoomDto = roomService.processMessage(messageDto, channel);
                channelRepository.applyToRoom(clientService.getProfile(channel.id().asLongText()).getRoomId(),
                        channelRoomDto::writeAndFlush);
                //kafkaProducerService.sendRoom(roomDto);
                break;
            case MESSAGE_LIST:
                ChannelMessageListDto channelMessageListDto = messageService.processMessageList(messageDto, channel);
                putChannel(channelMessageListDto.getRoomId(), channel);
                channelMessageListDto.writeAndFlush(channel);
                break;
            case CLIENT:
                ChannelClientDto channelClientDto = clientService.processMessage(messageDto, channel);
                putChannel(channelClientDto.getRoomId(), channel);
                break;
            default:
        }
    }
    @Override
    public boolean existsUserProfile(Channel channel) {
        return clientService.existsUserProfile(channel.id().asLongText());
    }
    @Override
    public void InitChannel(Channel channel) {
        UserProfileDto userProfileDto = clientService.getProfile(channel.id().asLongText());
        if (userProfileDto.getId() == null) {
            System.out.println(channel + " tourist");
        } else {
            roomService.getRoomList(userProfileDto.getId()).writeAndFlush(channel);
            userProfileDtoMapper.UserProfileDtoToChannelClientDto(userProfileDto).writeAndFlush(channel);
        }
    }
}
