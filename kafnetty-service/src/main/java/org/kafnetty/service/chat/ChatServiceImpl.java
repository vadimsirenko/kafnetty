package org.kafnetty.service.chat;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.kafnetty.dto.*;
import org.kafnetty.service.KafnettyProducer;
import org.kafnetty.session.Session;
import org.kafnetty.session.UserContext;
import org.kafnetty.store.repository.ChannelRepository;
import org.kafnetty.type.OperationType;
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
    public void processMessage(BaseDto messageDto, Channel channel, String groupId) {
        messageDto.setClusterId(groupId);
        switch (messageDto.getMessageType()) {
            case MESSAGE -> {
                MessageDto channelMessageDto = messageService.processMessage(messageService.createMessage(messageDto, channel), groupId);
                channelRepository.sendToRoom(channelMessageDto.getRoomId(), channelMessageDto);
                kafnettyProducer.sendMessage(channelMessageDto,
                        dto -> messageService.setMessageAsSent((MessageDto) dto));
            }
            case ROOM -> {
                RoomDto channelRoomDto = roomService.processMessage((RoomDto) messageDto, groupId);
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
                UserDto channelUserDto = clientService.processMessage((UserDto) messageDto, channel, groupId);
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
    public void processBaseDtoFromKafka(ConsumerRecord<UUID, BaseDto> consumerRecord, String groupId) {
        BaseDto message = consumerRecord.value();
        log.info("receive value : {} ", message.toJson());
        if (message instanceof MessageDto) {
            if (!message.getClusterId().equals(groupId)) {
                MessageDto channelMessageDto = (MessageDto) message;
                messageService.processMessage(channelMessageDto, groupId);
                channelRepository.sendToRoom(channelMessageDto.getRoomId(), channelMessageDto);
            }
        } else if (message instanceof RoomDto) {
            if (!message.getClusterId().equals(groupId)) {
                RoomDto channelRoomDto = (RoomDto) message;
                roomService.processMessage(channelRoomDto, groupId);
                channelRepository.sendToAllRoom(channelRoomDto);
            }
        } else if (message instanceof UserDto) {
            if (!message.getClusterId().equals(groupId)) {
                UserDto channelUserDto = (UserDto) message;
                clientService.processMessage(channelUserDto, null, groupId);
            }
        }
    }

    public void syncObjectCollections(String groupId) {
        for (UserDto channelUserDto : clientService.getNotSyncClients(groupId)) {
            kafnettyProducer.sendMessage(channelUserDto,
                    dto -> clientService.setClientAsSent((UserDto) dto));
        }
        for (RoomDto channelRoomDto : roomService.getNotSyncRooms(groupId)) {
            kafnettyProducer.sendMessage(channelRoomDto,
                    dto -> roomService.setRoomAsSent((RoomDto) dto));
        }
        for (MessageDto channelMessageDto : messageService.getNotSyncMessages(groupId)) {
            kafnettyProducer.sendMessage(channelMessageDto,
                    dto -> messageService.setMessageAsSent((MessageDto) dto));
        }
    }
}
