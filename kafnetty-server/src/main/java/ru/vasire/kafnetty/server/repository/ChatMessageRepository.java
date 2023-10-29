package ru.vasire.kafnetty.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vasire.kafnetty.entity.ChatMessage;

import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
    List<ChatMessage> findByRoomId(UUID roomId);
}
