package org.kafnetty.repository;

import org.kafnetty.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {
    Optional<Client> findByLogin(String login);
    List<Client> findAllByIsSentAndClusterId(boolean isSent, String clusterId);
}
