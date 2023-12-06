package org.kafnetty.service;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.kafnetty.dto.*;
import org.kafnetty.entity.User;
import org.kafnetty.kafka.config.KafnettyConsumerConfig;
import org.kafnetty.kafka.producer.KafnettyProducer;
import org.kafnetty.netty.handler.auth.Session;
import org.kafnetty.netty.handler.auth.UserContext;
import org.kafnetty.repository.ChannelRepository;
import org.kafnetty.type.OperationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {
    private final ClientService clientService;
    private final MessageService messageService;
    private final RoomService roomService;
    private final KafnettyProducer kafnettyProducer;
    private final ChannelRepository channelRepository;
    @Autowired
    private KafnettyConsumerConfig kafnettyConsumerConfig;

    @Override
    public void removeChannel(Channel channel) {
        Session session = UserContext.getContext(channel);
        if(session!=null) {
            channelRepository.removeChannelFromRoom(session.getRoomId(), channel,
                    oldGroup -> InfoDto.createLogoffInfo(session.getUser().getNickName()).writeAndFlush(oldGroup));
            clientService.removeChannelUser(channel.id().asLongText());
        }
    }

    @Override
    public void putChannel(UUID roomId, Channel channel) {
        Session session = UserContext.getContext(channel);
        assert session != null;
        UUID oldRoomId = session.getRoomId();
        channelRepository.changeRoom(roomId, oldRoomId, channel
                , oldGroup -> {
                    InfoDto.createLogoffInfo(session.getUser().getNickName()).writeAndFlush(oldGroup);
                }
                , group -> {
                    InfoDto.createLogonInfo(session.getUser().getNickName()).writeAndFlush(group);
                    clientService.setRoomForChannelUser(roomId, channel.id().asLongText());
                });
        UserContext.setRoom(channel, roomId);
    }

    @Override
    public void processMessage(BaseDto messageDto, Channel channel) {
        messageDto.setClusterId(kafnettyConsumerConfig.getGroupId());
        switch (messageDto.getMessageType()) {
            case MESSAGE -> {
                MessageDto channelMessageDto = messageService.processMessage(messageService.createMessage(messageDto, channel));
                channelRepository.sendToRoom(channelMessageDto.getRoomId(), channelMessageDto);
                kafnettyProducer.sendMessage(channelMessageDto,
                        dto -> messageService.setMessageAsSent((MessageDto) dto));
            }
            case ROOM -> {
                RoomDto channelRoomDto = roomService.processMessage((RoomDto) messageDto);
                channelRepository.sendToAllRoom(channelRoomDto);
                kafnettyProducer.sendMessage(channelRoomDto,
                        dto -> roomService.setRoomAsSent((RoomDto) dto));
            }
            case MESSAGE_LIST -> {
                MessageListDto channelMessageListDto = messageService.processMessageList((MessageListDto) messageDto);
                putChannel(channelMessageListDto.getRoomId(), channel);
                channelMessageListDto.writeAndFlush(channel);
            }
            case USER -> {
                UserDto channelUserDto = clientService.processMessage((UserDto) messageDto, channel);
                //putChannel(channelUserDto.getRoomId(), channel);
                if (channelUserDto.getOperationType() == OperationType.UPDATE ||
                        channelUserDto.getOperationType() == OperationType.CREATE) {
                    kafnettyProducer.sendMessage(channelUserDto,
                            dto -> clientService.setClientAsSent((UserDto) dto));
                }
                channelUserDto.writeAndFlush(channel);
            }
            default -> {
            }
        }
    }

    @Override
    public void InitChannel(Channel channel) {
        if(UserContext.hasContext(channel)) {
            roomService.getRoomList(UserContext.getContext(channel).getUser().getId()).writeAndFlush(channel);
        }
    }

    @Override
    public void processBaseDtoFromKafka(ConsumerRecord<UUID, BaseDto> consumerRecord) {
        BaseDto message = consumerRecord.value();
        log.info("receive value : {} ", message.toJson());
        if (message instanceof MessageDto) {
            if (!message.getClusterId().equals(kafnettyConsumerConfig.getGroupId())) {
                MessageDto channelMessageDto = (MessageDto) message;
                messageService.processMessage(channelMessageDto);
                channelRepository.sendToRoom(channelMessageDto.getRoomId(), channelMessageDto);
            }
        } else if (message instanceof RoomDto) {
            if (!message.getClusterId().equals(kafnettyConsumerConfig.getGroupId())) {
                RoomDto channelRoomDto = (RoomDto) message;
                roomService.processMessage(channelRoomDto);
                channelRepository.sendToAllRoom(channelRoomDto);
            }
        } else if (message instanceof UserDto) {
            if (!message.getClusterId().equals(kafnettyConsumerConfig.getGroupId())) {
                UserDto channelUserDto = (UserDto) message;
                clientService.processMessage(channelUserDto, null);
            }
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void syncObjectCollections() {
        for (UserDto channelUserDto : clientService.getNotSyncClients()) {
            kafnettyProducer.sendMessage(channelUserDto,
                    dto -> clientService.setClientAsSent((UserDto) dto));
        }
        for (RoomDto channelRoomDto : roomService.getNotSyncRooms()) {
            kafnettyProducer.sendMessage(channelRoomDto,
                    dto -> roomService.setRoomAsSent((RoomDto) dto));
        }
        for (MessageDto channelMessageDto : messageService.getNotSyncMessages()) {
            kafnettyProducer.sendMessage(channelMessageDto,
                    dto -> messageService.setMessageAsSent((MessageDto) dto));
        }
    }
}
