package ru.vasire.kafnetty.server.service.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vasire.kafnetty.server.dto.*;
import ru.vasire.kafnetty.server.entity.Client;
import ru.vasire.kafnetty.server.mapper.UserProfileDtoMapper;
import ru.vasire.kafnetty.server.netty.ChannelRepository;

import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@Service
@RequiredArgsConstructor
public class RequestProcessService {
    private static final String HTTP_PARAM_REQUEST = "request";
    private final ClientService clientService;
    private final ChatMessageService chatMessageService;
    private final RoomService roomService;
    private final ChannelRepository channelRepository;

    public void processWebSocketRequest(Channel channel, WebSocketFrame frame) {
        try {
            if (!clientService.existsUserProfile(channel.id().asLongText())) {
                channel.writeAndFlush(ErrorDto.createCommonError("You can't chat without logging in").toWebSocketFrame());
            } else {
                processReceiveMessage(((TextWebSocketFrame) frame).text(), channel);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void processReceiveMessage(String jsonMessage, Channel channel) {
        UserProfileDto userProfileDto = clientService.getProfile(channel.id().asLongText());
        BaseDto messageDto = BaseDto.encode(jsonMessage, BaseDto.class);
        switch (messageDto.getMessageType()) {
            case MESSAGE:
                ChatMessageDto m = chatMessageService.processRequest(jsonMessage);
                channelRepository.getRoomChannels(userProfileDto.getRoomId()).writeAndFlush(m.toWebSocketFrame());
                break;
            case ROOM:
                RoomDto r = roomService.processRequest(jsonMessage);
                channelRepository.getRoomChannels(userProfileDto.getRoomId()).writeAndFlush(r.toWebSocketFrame());
                break;
            case MESSAGE_LIST:
                MessageListDto ml = chatMessageService.processMessageListRequest(jsonMessage);
                channelRepository.put(ml.getRoomId(), channel);
                channel.writeAndFlush(ml.toWebSocketFrame());
                break;
            default:
        }
    }

    public boolean processHttpRequest(ChannelHandlerContext ctx, HttpRequest request) {
        Client client;
        // Handle a bad request.
        if (!request.decoderResult().isSuccess()) {
            sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
            return false;
        }

        // Allow only GET methods.
        if (request.method() != GET) {
            sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
            return false;
        }

        if ("/favicon.ico".equals(request.uri()) || "/".equals(request.uri())) {
            sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
            return false;
        }

        Map<String, List<String>> requestParams = new QueryStringDecoder(request.uri()).parameters();

        if (requestParams.isEmpty() || !requestParams.containsKey(HTTP_PARAM_REQUEST)) {
            System.err.println(HTTP_PARAM_REQUEST + " parameters are not default");
            sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
            return false;
        }
        client = clientService.clientLogin(requestParams.get(HTTP_PARAM_REQUEST).get(0), ctx.channel().id().asLongText());

        if (client.getRoomId() == null) {
            System.err.println("Room number is not default");
            sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
            return false;
        }
        channelRepository.put(client.getRoomId(), ctx.channel());
        return true;
    }

    public void InitChannel(Channel channel) {
        UserProfileDto userProfileDto = clientService.getProfile(channel.id().asLongText());
        if (userProfileDto.getId() == null) {
            System.out.println(channel + " tourist");
        } else {
            channel.writeAndFlush(roomService.getRoomList(userProfileDto.getId()).toWebSocketFrame());
            channel.writeAndFlush(UserProfileDtoMapper.INSTANCE.UserProfileDtoToClientDto(userProfileDto).toWebSocketFrame());
        }
    }

    private void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req, FullHttpResponse res) {
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
}
