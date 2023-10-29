package ru.vasire.kafnetty.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vasire.kafnetty.entity.Client;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {
    Optional<Client> findByLogin(String login);
}
