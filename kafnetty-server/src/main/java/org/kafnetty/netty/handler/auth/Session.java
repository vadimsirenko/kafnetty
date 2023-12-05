package org.kafnetty.netty.handler.auth;

import lombok.Getter;
import lombok.Setter;
import org.kafnetty.entity.User;

import java.util.UUID;

@Setter
@Getter
public class Session {
    private User user;
    private UUID RoomId;

    public Session(User user) {
        this.user = user;
    }
}
