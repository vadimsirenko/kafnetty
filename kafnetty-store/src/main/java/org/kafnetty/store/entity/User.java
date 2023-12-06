package org.kafnetty.store.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.kafnetty.type.Role;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "_user", uniqueConstraints = {
        @UniqueConstraint(name = "UK_user_email", columnNames = {"email"}),
        @UniqueConstraint(name = "UK_user_nick_name", columnNames = {"nick_name"})})
public class User {
    @Id
    private UUID id;
    private String email;
    private String fullName;
    @Column(name = "nick_name")
    private String nickName;
    private String password;
    private boolean sent = false;
    @Enumerated(EnumType.STRING)
    private Role role;
}
