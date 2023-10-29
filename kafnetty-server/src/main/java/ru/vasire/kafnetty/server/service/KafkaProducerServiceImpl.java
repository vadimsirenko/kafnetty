package ru.vasire.kafnetty.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.vasire.kafnetty.dto.ChatMessageDto;
import ru.vasire.kafnetty.dto.RoomDto;
import ru.vasire.kafnetty.dto.UserProfileDto;

@Service
public class KafkaProducerServiceImpl implements KafkaProducerService {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducerServiceImpl.class);
    @Override
    public void sendMessage(ChatMessageDto chatMessageDto) {
        log.info("send message: " + chatMessageDto.getId());
    }

    @Override
    public void sendRoom(RoomDto roomDto) {
        log.info("send room: " + roomDto.getId());
    }

    @Override
    public void sendUserProfile(UserProfileDto userProfileDto) {
        log.info("send userProfile: " + userProfileDto.getId());
    }
}
