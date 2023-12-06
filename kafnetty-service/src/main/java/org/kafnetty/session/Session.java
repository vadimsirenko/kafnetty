package org.kafnetty.session;

import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import lombok.Getter;
import lombok.Setter;
import org.kafnetty.dto.UserDto;

import java.util.UUID;

@Setter
@Getter
public class Session {
    private UserDto user;
    private UUID roomId;
    private String clusterId;
    private WebSocketServerHandshaker handshaker;

    public Session(UserDto user) {
        this.user = user;
    }
}
