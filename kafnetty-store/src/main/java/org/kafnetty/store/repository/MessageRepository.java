package org.kafnetty.store.repository;

import org.kafnetty.store.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByRoomIdOrderByTs(UUID roomId);

    List<Message> findBySentAndClusterId(boolean isSent, String clusterId);
}
