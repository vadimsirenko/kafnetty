package org.kafnetty.repository;

import org.kafnetty.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByRoomId(UUID roomId);
}