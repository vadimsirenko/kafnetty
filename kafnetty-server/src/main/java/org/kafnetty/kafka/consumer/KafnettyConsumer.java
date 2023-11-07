package org.kafnetty.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kafnetty.dto.channel.ChannelClientDto;
import org.kafnetty.dto.channel.ChannelMessageDto;
import org.kafnetty.dto.channel.ChannelRoomDto;
import org.kafnetty.dto.kafka.KafkaBaseDto;
import org.kafnetty.dto.kafka.KafkaClientDto;
import org.kafnetty.dto.kafka.KafkaMessageDto;
import org.kafnetty.dto.kafka.KafkaRoomDto;
import org.kafnetty.mapper.ClientMapper;
import org.kafnetty.mapper.MessageMapper;
import org.kafnetty.mapper.RoomMapper;
import org.kafnetty.repository.ChannelRepository;
import org.kafnetty.service.ClientService;
import org.kafnetty.service.MessageService;
import org.kafnetty.service.RoomService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafnettyConsumer {
    @Value("${spring.kafka.group-id}")
    private String groupId;

    private final MessageMapper messageMapper;
    private final RoomMapper roomMapper;
    private final ClientMapper clientMapper;
    private final MessageService messageService;
    private final RoomService roomService;
    private final ClientService clientService;
    private final ChannelRepository channelRepository;

    /*
        @RetryableTopic(kafkaTemplate = "kafkaTemplate",
                exclude = {DeserializationException.class,
                        MessageConversionException.class,
                        ConversionException.class,
                        MethodArgumentResolutionException.class,
                        NoSuchMethodException.class,
                        ClassCastException.class},
                attempts = "4",
                backoff = @Backoff(delay = 3000, multiplier = 1.5, maxDelay = 15000)
        )
        */
    @KafkaListener(topics = "${spring.kafka.topic.name}", groupId = "${spring.kafka.group-id}")
    public void process(KafkaBaseDto message) {
        log.info("polled records.counter:{}", message.getKafkaMessageId());
        try {
            if (message instanceof KafkaMessageDto) {

                if (!message.getClusterId().equals(groupId)) {
                    ChannelMessageDto channelMessageDto = messageMapper.KafkaMessageDtoToChannelMessageDto((KafkaMessageDto) message);
                    messageService.processMessage(channelMessageDto);
                    channelRepository.sendToRoom(channelMessageDto.getRoomId(), channelMessageDto);
                }
            } else if (message instanceof KafkaRoomDto) {
                if (!message.getClusterId().equals(groupId)) {
                    ChannelRoomDto channelRoomDto = roomMapper.KafkaRoomDtoToChannelRoomDto((KafkaRoomDto) message);
                    roomService.processMessage(channelRoomDto);
                    channelRepository.sendToAllRoom(channelRoomDto);
                }
            } else if (message instanceof KafkaClientDto) {
                if (!message.getClusterId().equals(groupId)) {
                    ChannelClientDto channelClientDto = clientMapper.KafkaClientDtoToChannelClientDto((KafkaClientDto) message);
                    clientService.processMessage(channelClientDto, null);
                }
            }
            log.info("receive value:{}", message.toJson());
        } catch (Exception ex) {
            log.error("can't parse record:{}", message, ex);
        }
    }
}
