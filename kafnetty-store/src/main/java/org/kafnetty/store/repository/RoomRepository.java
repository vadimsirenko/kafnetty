package org.kafnetty.store.repository;

import org.kafnetty.store.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {
    Room findByName(String name);

    List<Room> findBySentAndClusterId(boolean isSent, String clusterId);
}
