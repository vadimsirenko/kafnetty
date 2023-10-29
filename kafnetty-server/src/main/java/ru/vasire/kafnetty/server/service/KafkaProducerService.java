package ru.vasire.kafnetty.server.service;

import ru.vasire.kafnetty.dto.ChatMessageDto;
import ru.vasire.kafnetty.dto.RoomDto;
import ru.vasire.kafnetty.dto.UserProfileDto;

public interface KafkaProducerService {
    void sendMessage(ChatMessageDto chatMessageDto);
    void sendRoom(RoomDto roomDto);
    void sendUserProfile(UserProfileDto userProfileDto);
}
