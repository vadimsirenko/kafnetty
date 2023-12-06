package org.kafnetty.session;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.util.AttributeKey;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class UserContext {
    public static final AttributeKey<Session> SESSION_ATTR_KEY = AttributeKey.valueOf("SESSION");

    public static void setRoom(Channel channel, @NonNull UUID roomId) {
        if(channel.hasAttr(SESSION_ATTR_KEY) && !roomId.equals(channel.attr(SESSION_ATTR_KEY).get().getRoomId())){
            Session session = channel.attr(SESSION_ATTR_KEY).get();
            session.setRoomId(roomId);
            channel.attr(SESSION_ATTR_KEY).set(session);
        }
    }

    public static void setContext(Channel channel, Session session) {
        channel.attr(SESSION_ATTR_KEY).set(session);
    }

    public static boolean hasContext(Channel channel) {
        return channel.hasAttr(SESSION_ATTR_KEY);
    }

    public static Session getContext(Channel channel) {
        return channel.attr(SESSION_ATTR_KEY).get();
    }

    public static void setHandshaker(Channel channel,WebSocketServerHandshaker handshaker) {
        if(channel.hasAttr(SESSION_ATTR_KEY)){
            Session session = channel.attr(SESSION_ATTR_KEY).get();
            session.setHandshaker(handshaker);
            channel.attr(SESSION_ATTR_KEY).set(session);
        }
    }
}
