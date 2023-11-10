package org.kafnetty.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "client", uniqueConstraints = {
        @UniqueConstraint(name = "UK_Client_Login", columnNames = {"login"}),
        @UniqueConstraint(name = "UK_Client_nick_name", columnNames = {"nick_name"}),
        @UniqueConstraint(name = "UK_Client_email", columnNames = {"email"})})
public class Client {
    @Id
    private UUID id;
    @Column(name = "cluster_id", nullable = false)
    private String clusterId;
    @Column(name = "login", nullable = false)
    private String login;
    private String email;
    @Column(name = "nick_name")
    private String nickName;
    private String token;
    @Transient
    private UUID roomId;
    private Long ts;
    @Column(name = "is_sent")
    private boolean isSent = false;
}
