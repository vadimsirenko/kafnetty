package ru.vasire.kafnetty.server.processors;

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
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.vasire.kafnetty.server.dto.ErrorDto;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public interface HttpRequestProcessor{
    public final String HTTP_PARAM_REQUEST = "request";

    public void processWebSocketRequest(Channel channel, WebSocketFrame frame);

    public boolean processHttpRequest(ChannelHandlerContext ctx, HttpRequest request);

    void InitChannel(Channel channel);

    void removeChannel(Channel channel);
}
