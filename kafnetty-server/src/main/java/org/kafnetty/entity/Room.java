package org.kafnetty.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "room", uniqueConstraints = @UniqueConstraint(name = "UK_Room_Name", columnNames = {"name"}))
public class Room {
    @Id
    @Column(name = "id")
    private UUID id;
    @Column(name = "cluster_id", nullable = false)
    private String clusterId;
    @Column(name = "name", nullable = false)
    private String name;
    @Transient
    private long messageCount;
    @Column(name = "is_sent")
    private boolean isSent = false;
}
