package org.kafnetty.netty.handler.auth;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.UnsupportedFormatException;
import org.kafnetty.dto.BaseDto;
import org.kafnetty.dto.TokenDto;
import org.kafnetty.dto.UserDto;
import org.kafnetty.netty.handler.http.HttpProcessor;
import org.kafnetty.netty.handler.BaseWebSocketServerHandler;
import org.kafnetty.service.AuthenticationService;
import org.kafnetty.type.OperationType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static org.kafnetty.config.ServerConstants.LOGON_PATH;

@Component
@ChannelHandler.Sharable
@Qualifier("authenticationHandler")
@Slf4j
public class AuthenticationHandler extends BaseWebSocketServerHandler<FullHttpRequest> {
    private final HttpProcessor httpProcessor;
    private final AuthenticationService authenticationService;

    public AuthenticationHandler(HttpProcessor httpProcessor, AuthenticationService authenticationService) {
        super(false);
        this.httpProcessor = httpProcessor;
        this.authenticationService = authenticationService;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        // Handle a bad request.
        if (!request.decoderResult().isSuccess()) {
            httpProcessor.sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
        } else if (request.method() == POST && request.uri().equals(LOGON_PATH)) {
            try {
                httpProcessor.sendHttpJsonResponse(ctx, request, OK, createUserToken(request));
            } catch (UnsupportedFormatException ex) {
                httpProcessor.sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
                log.error("error at process Http request", ex);
            }
            ctx.fireChannelReadComplete();
        }
        ctx.fireChannelRead(request);
    }
    public TokenDto createUserToken(FullHttpRequest request) throws UnsupportedFormatException {
        ByteBuf jsonBuf = request.content();
        String jsonStr = jsonBuf.toString(CharsetUtil.UTF_8);
        BaseDto baseDto = BaseDto.decode(jsonStr);
        final UserDto userDto;
        if (!(baseDto instanceof UserDto)) {
            throw new UnsupportedFormatException("Unsupported format");
        }else{
            userDto = (UserDto) baseDto;
            if(baseDto.getOperationType()== OperationType.CREATE){
                return authenticationService.register(userDto);
            } else {
                return authenticationService.authenticate(userDto);
            }
        }
    }
}
