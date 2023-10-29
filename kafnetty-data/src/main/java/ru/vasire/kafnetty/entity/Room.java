package ru.vasire.kafnetty.entity;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "room", uniqueConstraints = @UniqueConstraint(name = "UK_Room_Name", columnNames={"name"} ))
public class Room {
    @Id
    @Column(name = "id")
    private UUID id;
    @Column(name = "name", nullable = false)
    private String name;
    @Transient
    private long messageCount;
}
