package org.kafnetty.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "message")
public class Message {
    @Id
    @NonNull
    private UUID id;
    @Column(name = "cluster_id", nullable = false)
    private String clusterId;
    @Column(name = "sender_id", nullable = false)
    private UUID senderId;
    @Column(name = "room_id", nullable = false)
    private UUID roomId;
    @Column(name = "message_text", nullable = false)
    private String messageText;
    private Long ts;
    private String sender;
    @Column(name = "is_sent")
    private boolean isSent = false;
}
