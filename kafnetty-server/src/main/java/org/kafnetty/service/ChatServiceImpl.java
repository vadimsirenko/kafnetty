package org.kafnetty.service;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kafnetty.dto.UserProfileDto;
import org.kafnetty.dto.channel.*;
import org.kafnetty.dto.kafka.KafkaMessageDto;
import org.kafnetty.dto.kafka.KafkaRoomDto;
import org.kafnetty.mapper.MessageMapper;
import org.kafnetty.mapper.RoomMapper;
import org.kafnetty.mapper.UserProfileDtoMapper;
import org.kafnetty.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {
    private final ClientService clientService;
    private final MessageService messageService;
    private final RoomService roomService;
    private final KafkaProducerService kafkaProducerService;
    private final ChannelRepository channelRepository;

    //private final Map<UUID, ChannelGroup> CHANNEL_GROUP_MAP = new ConcurrentHashMap<>();
    @Autowired
    private UserProfileDtoMapper userProfileDtoMapper;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private RoomMapper roomMapper;

    @Override
    public void putChannel(UUID roomId, Channel channel) {
        UserProfileDto userProfileDto = clientService.getProfile(channel.id().asLongText());
        UUID oldRoomId = (userProfileDto != null) ? userProfileDto.getRoomId() : null;
        channelRepository.changeRoom(roomId, oldRoomId, channel
                , oldGroup -> {
                    assert userProfileDto != null;
                    ChannelInfoDto.createLogoffInfo(userProfileDto.getNickName()).writeAndFlush(oldGroup);
                }
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
                oldGroup -> ChannelInfoDto.createLogoffInfo(userProfileDto.getNickName()).writeAndFlush(oldGroup));
        clientService.removeProfile(channel.id().asLongText());
    }

    @Override
    public void processMessage(String jsonMessage, Channel channel) {
        ChannelBaseDto messageDto = ChannelBaseDto.decode(jsonMessage);
        switch (messageDto.getMessageType()) {
            case MESSAGE:
                ChannelMessageDto channelMessageDto = messageService.processLocalMessage((ChannelMessageDto) messageDto, channel);
                channelRepository.sendToRoom(clientService.getProfile(channel.id().asLongText()).getRoomId(), channelMessageDto);
                kafkaProducerService.sendMessage(messageMapper.ChannelMessageDtoToKafkaMessageDto(channelMessageDto),
                        kafkaDto -> messageService.setMessageAsSended(messageMapper.KafkaMessageDtoToChannelMessageDto((KafkaMessageDto) kafkaDto)));
                break;
            case ROOM:
                ChannelRoomDto channelRoomDto = roomService.processLocalMessage((ChannelRoomDto) messageDto, channel);
                channelRepository.sendToRoom(clientService.getProfile(channel.id().asLongText()).getRoomId(), channelRoomDto);
                kafkaProducerService.sendRoom(roomMapper.ChannelRoomDtoToKafkaRoomDto(channelRoomDto),
                        kafkaDto -> roomService.setMessageAsSended(roomMapper.KafkaRoomDtoToChannelRoomDto((KafkaRoomDto) kafkaDto)));
                break;
            case MESSAGE_LIST:
                ChannelMessageListDto channelMessageListDto = messageService.processMessageList((ChannelMessageListDto) messageDto, channel);
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
