package org.kafnetty.service;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.kafnetty.dto.*;
import org.kafnetty.kafka.config.KafnettyConsumerConfig;
import org.kafnetty.kafka.producer.KafnettyProducer;
import org.kafnetty.repository.ChannelRepository;
import org.kafnetty.type.OPERATION_TYPE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
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

    private void putChannel(UUID roomId, Channel channel) {
        ClientDto clientDto = clientService.getChannelUser(channel.id().asLongText());
        UUID oldRoomId = (clientDto != null) ? clientDto.getRoomId() : null;
        channelRepository.changeRoom(roomId, oldRoomId, channel
                , oldGroup -> {
                    assert clientDto != null;
                    InfoDto.createLogoffInfo(clientDto.getNickName()).writeAndFlush(oldGroup);
                }
                , group -> {
                    assert clientDto != null;
                    InfoDto.createLogonInfo(clientDto.getNickName()).writeAndFlush(group);
                    clientService.setRoomForChannelUser(roomId, channel.id().asLongText());
                });
    }

    @Override
    public void removeChannel(Channel channel) {
        ClientDto clientDto = clientService.getChannelUser(channel.id().asLongText());
        UUID roomIdOld = (clientDto != null) ? clientDto.getRoomId() : null;
        assert clientDto != null;
        channelRepository.removeChannelFromRoom(roomIdOld, channel,
                oldGroup -> InfoDto.createLogoffInfo(clientDto.getNickName()).writeAndFlush(oldGroup));
        clientService.removeChannelUser(channel.id().asLongText());
    }

    @Override
    public void processMessage(String jsonMessage, Channel channel) {
        BaseDto messageDto = BaseDto.decode(jsonMessage);
        messageDto.setClusterId(kafnettyConsumerConfig.getGroupId());
        switch (messageDto.getMessageType()) {
            case MESSAGE -> {
                MessageDto channelMessageDto = messageService.processMessage((MessageDto) messageDto);
                channelRepository.sendToRoom(clientService.getChannelUser(channel.id().asLongText()).getRoomId(), channelMessageDto);
                kafnettyProducer.sendMessage(channelMessageDto,
                        dto -> messageService.setMessageAsSended((MessageDto) dto));
            }
            case ROOM -> {
                RoomDto channelRoomDto = roomService.processMessage((RoomDto) messageDto);
                channelRepository.sendToAllRoom(channelRoomDto);
                kafnettyProducer.sendMessage(channelRoomDto,
                        dto -> roomService.setRoomAsSended((RoomDto) dto));
            }
            case MESSAGE_LIST -> {
                MessageListDto channelMessageListDto = messageService.processMessageList((MessageListDto) messageDto);
                putChannel(channelMessageListDto.getRoomId(), channel);
                channelMessageListDto.writeAndFlush(channel);
            }
            case CLIENT -> {
                ClientDto channelClientDto = clientService.processMessage((ClientDto) messageDto, channel);
                putChannel(channelClientDto.getRoomId(), channel);
                if (channelClientDto.getOperationType() == OPERATION_TYPE.UPDATE ||
                        channelClientDto.getOperationType() == OPERATION_TYPE.CREATE) {
                    kafnettyProducer.sendMessage(channelClientDto,
                            dto -> clientService.setClientAsSended((ClientDto) dto));
                }
                channelClientDto.writeAndFlush(channel);
            }
            default -> {
            }
        }
    }

    @Override
    public boolean existsChannelUser(Channel channel) {
        return clientService.existsChannelUser(channel.id().asLongText());
    }

    @Override
    public void InitChannel(Channel channel) {
        ClientDto clientDto = clientService.getChannelUser(channel.id().asLongText());
        if (clientDto.getId() == null) {
            System.out.println(channel + " tourist");
        } else {
            roomService.getRoomList(clientDto.getId()).writeAndFlush(channel);
            clientDto.writeAndFlush(channel);
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
        } else if (message instanceof ClientDto) {
            if (!message.getClusterId().equals(kafnettyConsumerConfig.getGroupId())) {
                ClientDto channelClientDto = (ClientDto) message;
                clientService.processMessage(channelClientDto, null);
            }
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void syncObjectCollections() {
        for (ClientDto channelClientDto : clientService.getNotSyncClients()) {
            kafnettyProducer.sendMessage(channelClientDto,
                    dto -> clientService.setClientAsSended((ClientDto) dto));
        }
        for (RoomDto channelRoomDto : roomService.getNotSyncRooms()) {
            kafnettyProducer.sendMessage(channelRoomDto,
                    dto -> roomService.setRoomAsSended((RoomDto) dto));
        }
        for (MessageDto channelMessageDto : messageService.getNotSyncMessages()) {
            kafnettyProducer.sendMessage(channelMessageDto,
                    dto -> messageService.setMessageAsSended((MessageDto) dto));
        }
    }
}
