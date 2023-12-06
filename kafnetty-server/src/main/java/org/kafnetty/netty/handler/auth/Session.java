package org.kafnetty.netty.handler.auth;

import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import lombok.Getter;
import lombok.Setter;
import org.kafnetty.entity.User;

import java.util.UUID;

@Setter
@Getter
public class Session {
    private User user;
    private UUID roomId;
    private WebSocketServerHandshaker handshaker;

    public Session(User user) {
        this.user = user;
    }
}
