package org.kafnetty.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kafnetty.dto.BaseDto;
import org.kafnetty.dto.ClientDto;
import org.kafnetty.dto.MessageDto;
import org.kafnetty.dto.RoomDto;
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
    private final MessageService messageService;
    private final RoomService roomService;
    private final ClientService clientService;
    private final ChannelRepository channelRepository;
    @Value("${spring.kafka.group-id}")
    private String groupId;

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
    public void process(BaseDto message) {
        log.info("polled records.counter:{}", message.getId());
        try {
            if (message instanceof MessageDto) {

                if (!message.getClusterId().equals(groupId)) {
                    MessageDto channelMessageDto = (MessageDto) message;
                    messageService.processMessage(channelMessageDto);
                    channelRepository.sendToRoom(channelMessageDto.getRoomId(), channelMessageDto);
                }
            } else if (message instanceof RoomDto) {
                if (!message.getClusterId().equals(groupId)) {
                    RoomDto channelRoomDto = (RoomDto) message;
                    roomService.processMessage(channelRoomDto);
                    channelRepository.sendToAllRoom(channelRoomDto);
                }
            } else if (message instanceof ClientDto) {
                if (!message.getClusterId().equals(groupId)) {
                    ClientDto channelClientDto = (ClientDto) message;
                    clientService.processMessage(channelClientDto, null);
                }
            }
            log.info("receive value:{}", message.toJson());
        } catch (Exception ex) {
            log.error("can't parse record:{}", message.toJson(), ex);
        }
    }
}
