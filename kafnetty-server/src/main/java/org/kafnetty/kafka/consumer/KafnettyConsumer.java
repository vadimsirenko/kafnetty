package org.kafnetty.kafka.consumer;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.kafnetty.dto.channel.ChannelMessageDto;
import org.kafnetty.dto.kafka.KafkaBaseDto;
import org.kafnetty.dto.kafka.KafkaMessageDto;
import org.kafnetty.mapper.MessageMapper;
import org.kafnetty.repository.ChannelRepository;
import org.kafnetty.service.ClientService;
import org.kafnetty.service.MessageService;
import org.kafnetty.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.kafnetty.kafka.consumer.BaseConsumer.MAX_POLL_INTERVAL_MS;

@Service
@RequiredArgsConstructor
public class KafnettyConsumer {
    private static final Logger log = LoggerFactory.getLogger(KafnettyConsumer.class);

    private final MessageConsumer messageKafkaConsumer;
    private final RoomConsumer roomConsumer;
    private final ClientConsumer clientConsumer;
    private final MessageService messageService;
    private final RoomService roomService;
    private final ClientService clientService;
    private final ChannelRepository channelRepository;
    private final MessageMapper messageMapper;
    private final Duration timeout = Duration.ofMillis(2_000);

    private final ScheduledExecutorService executorMessages = Executors.newScheduledThreadPool(1);
    private final ScheduledExecutorService executorRooms = Executors.newScheduledThreadPool(1);
    private final ScheduledExecutorService executorClients = Executors.newScheduledThreadPool(1);

    public void startConsuming() {
        // executor.scheduleAtFixedRate(this::poll, 0, MAX_POLL_INTERVAL_MS * 2L, TimeUnit.MILLISECONDS);
        executorMessages.scheduleAtFixedRate(this::messagePoll, 0, MAX_POLL_INTERVAL_MS / 2L, TimeUnit.MILLISECONDS);
        executorRooms.scheduleAtFixedRate(this::roomPoll, 0, MAX_POLL_INTERVAL_MS / 2L, TimeUnit.MILLISECONDS);
        executorClients.scheduleAtFixedRate(this::clientPoll, 0, MAX_POLL_INTERVAL_MS / 2L, TimeUnit.MILLISECONDS);
    }

    private void messagePoll() {
        log.info("poll records");
        ConsumerRecords<UUID, KafkaBaseDto> records = messageKafkaConsumer.getKafkaConsumer().poll(timeout);
        //       sleep();
        log.info("polled records.counter:{}", records.count());
        for (ConsumerRecord<UUID, KafkaBaseDto> kafkaRecord : records) {
            try {
                var key = kafkaRecord.key();
                var value = kafkaRecord.value();
                if(!value.getClusterId().equals(messageKafkaConsumer.CLUSTER_ID) && value instanceof KafkaMessageDto) {
                    ChannelMessageDto channelMessageDto = messageMapper.KafkaMessageDtoToChannelMessageDto((KafkaMessageDto)value);
                    messageService.processMessage(channelMessageDto);
                    channelRepository.applyToRoom(channelMessageDto.getRoomId(), channelMessageDto::writeAndFlush);
                }
                log.info("key:{}, value:{}, record:{}", key, value, kafkaRecord);
            } catch (Exception ex) {
                log.error("can't parse record:{}", kafkaRecord, ex);
            }
        }
    }

    private void roomPoll() {
        log.info("poll records");
        ConsumerRecords<UUID, KafkaBaseDto> records = roomConsumer.getKafkaConsumer().poll(timeout);
        //       sleep();
        log.info("polled records.counter:{}", records.count());
        for (ConsumerRecord<UUID, KafkaBaseDto> kafkaRecord : records) {
            try {
                var key = kafkaRecord.key();
                var value = kafkaRecord.value();
                log.info("key:{}, value:{}, record:{}", key, value, kafkaRecord);
                //dataConsumer.accept(value);
            } catch (Exception ex) {
                log.error("can't parse record:{}", kafkaRecord, ex);
            }
        }
    }

    private void clientPoll() {
        log.info("poll records");
        ConsumerRecords<UUID, KafkaBaseDto> records = clientConsumer.getKafkaConsumer().poll(timeout);
        //       sleep();
        log.info("polled records.counter:{}", records.count());
        for (ConsumerRecord<UUID, KafkaBaseDto> kafkaRecord : records) {
            try {
                var key = kafkaRecord.key();
                var value = kafkaRecord.value();
                log.info("key:{}, value:{}, record:{}", key, value, kafkaRecord);
                //dataConsumer.accept(value);
            } catch (Exception ex) {
                log.error("can't parse record:{}", kafkaRecord, ex);
            }
        }
    }
    @PreDestroy
    public void stopConsuming() {
        log.info("Stop receiving data from topics");
        executorMessages.shutdown();
        executorRooms.shutdown();
        executorClients.shutdown();
    }
}