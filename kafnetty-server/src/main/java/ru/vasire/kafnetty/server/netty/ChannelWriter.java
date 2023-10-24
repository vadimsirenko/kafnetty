package ru.vasire.kafnetty.server.netty;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.CharsetUtil;
import org.springframework.stereotype.Component;
import ru.vasire.kafnetty.server.dto.BaseDto;
import ru.vasire.kafnetty.server.service.message.InfoService;

@Component
public class ChannelWriter {
    public void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req, FullHttpResponse res) {
        if (res.status().code() != HttpResponseStatus.OK.code()) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            HttpUtil.setContentLength(res, res.content().readableBytes());
        }
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpUtil.isKeepAlive(req) || res.status().code() != HttpResponseStatus.OK.code()) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    public void sendToRoom(ChannelGroup roomChannelGroup, BaseDto message) {
        String messageJson = null;
        try {
            messageJson = new ObjectMapper().writeValueAsString(message);
            roomChannelGroup.writeAndFlush(new TextWebSocketFrame(messageJson));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendToClient(Channel channel, BaseDto message) {
        String messageJson = null;
        try {
            messageJson = new ObjectMapper().writeValueAsString(message);
            channel.writeAndFlush(new TextWebSocketFrame(messageJson));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendLeaveTheRoomMessage(ChannelGroup roomChannelGroup, String nickName) {
        roomChannelGroup.writeAndFlush(decoreDtoToString(InfoService.getLogoffInfo(nickName)));
    }

    public void sendJoinToTheRoomMessage(ChannelGroup roomChannelGroup, String nickName) {
        roomChannelGroup.writeAndFlush(decoreDtoToString(InfoService.getLogonInfo(nickName)));
    }

    private static String decoreDtoToString(BaseDto dtoObject) {
        try {
            return new ObjectMapper().writeValueAsString(dtoObject);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
