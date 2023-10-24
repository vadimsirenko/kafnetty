package ru.vasire.kafnetty.server.service.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vasire.kafnetty.server.dto.*;
import ru.vasire.kafnetty.server.entity.Client;
import ru.vasire.kafnetty.server.mapper.ClientMapper;
import ru.vasire.kafnetty.server.mapper.UserProfileDtoMapper;
import ru.vasire.kafnetty.server.netty.ChannelRepository;
import ru.vasire.kafnetty.server.netty.ChannelWriter;

import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@Service
@RequiredArgsConstructor
public class RequestProcessService {
    private static final String HTTP_PARAM_REQUEST = "request";

    private final ClientService clientService;
    private final MessageService messageService;
    private final RoomService roomService;
    private final ChannelRepository channelRepository;
    private final ChannelWriter channelWriter;
    public void processWebSocketRequest(Channel channel, WebSocketFrame frame) {
        try {
            if (!clientService.existsUserProfile(channel.id().asLongText())) {
                ErrorDto res = new ErrorDto(1001, "You can't chat without logging in");
                String json = new ObjectMapper().writeValueAsString(res);
                channel.write(new TextWebSocketFrame(json));
            } else {
                String requestJson = ((TextWebSocketFrame) frame).text();
                System.out.println("Received " + channel + requestJson);

                BaseDto baseDto = new ObjectMapper().readValue(requestJson, BaseDto.class);

                BaseDto res = switch (baseDto.getMessageType()) {
                    case MESSAGE -> messageService.processRequest(requestJson);
                    case ROOM -> roomService.processRequest(requestJson);
                    case MESSAGE_LIST -> {
                        MessageListDto messageListDto = messageService.processMessageListRequest(requestJson);
                        channelRepository.put(messageListDto.getRoomId(), channel);
                        yield messageListDto;
                    }
                    default -> new ErrorDto(100, "Unknown request type" + baseDto.getMessageType());
                };
                sendMessage(channel, res);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public boolean processHttpRequest(ChannelHandlerContext ctx, HttpRequest request) {
        Client client;
        // Handle a bad request.
        if (!request.decoderResult().isSuccess()) {
            channelWriter.sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
            return false;
        }

        // Allow only GET methods.
        if (request.method() != GET) {
            channelWriter.sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
            return false;
        }

        if ("/favicon.ico".equals(request.uri()) || "/".equals(request.uri())) {
            channelWriter.sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
            return false;
        }

        Map<String, List<String>> requestParams = new QueryStringDecoder(request.uri()).parameters();

        if (requestParams.isEmpty() || !requestParams.containsKey(HTTP_PARAM_REQUEST)) {
            System.err.println(HTTP_PARAM_REQUEST + " parameters are not default");
            channelWriter.sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
            return false;
        }
        client = clientService.clientLogin(requestParams.get(HTTP_PARAM_REQUEST).get(0), ctx.channel().id().asLongText());

        if (client.getRoomId() == null) {
            System.err.println("Room number is not default");
            channelWriter.sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
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
            String json = null;
            try {

                RoomListDto roomListDto = roomService.getRoomList(userProfileDto.getId());
                json = new ObjectMapper().writeValueAsString(roomListDto);
                channel.writeAndFlush(new TextWebSocketFrame(json));

                ClientDto clientDto = UserProfileDtoMapper.INSTANCE.UserProfileDtoToClientDto(userProfileDto);
                json = new ObjectMapper().writeValueAsString(clientDto);
                channel.writeAndFlush(new TextWebSocketFrame(json));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void sendMessage(Channel channel, BaseDto message) {
        UserProfileDto userProfileDto = clientService.getProfile(channel.id().asLongText());
        if(userProfileDto!=null) {
            if (message instanceof RoomMessage) {
                channelWriter.sendToRoom(channelRepository.getRoomChannels(userProfileDto.getRoomId()), message);
            } else {
                channelWriter.sendToClient(channel, message);
            }
        }
    }
}
