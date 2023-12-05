package org.kafnetty.netty.handler.auth;

import io.netty.channel.Channel;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.UUID;
import static org.kafnetty.netty.handler.http.HttpServerHandler.SESSION_ATTR_KEY;

@Component
public class UserContext {

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
}
