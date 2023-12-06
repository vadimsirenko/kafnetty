package org.kafnetty.netty.handler.auth;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import lombok.extern.slf4j.Slf4j;
import org.kafnetty.dto.UserDto;
import org.kafnetty.service.auth.JwtService;
import org.kafnetty.netty.config.ServerConstants;
import org.kafnetty.session.Session;
import org.kafnetty.session.UserContext;
import org.kafnetty.netty.handler.http.HttpProcessor;
import org.kafnetty.netty.handler.BaseWebSocketServerHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@Component
@ChannelHandler.Sharable
@Qualifier("contextResolverHandler")
@Slf4j
public class JwtContextResolverHandler extends BaseWebSocketServerHandler<FullHttpRequest> {
    private final HttpProcessor httpProcessor;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtContextResolverHandler(HttpProcessor httpProcessor, UserDetailsService userDetailsService,
                                     JwtService jwtService) {
        super(false);
        this.httpProcessor = httpProcessor;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        try {
            // Handle a bad request.
            if (!request.decoderResult().isSuccess()) {
                httpProcessor.sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
                ctx.fireChannelReadComplete();
                return;
            }
            // GET methods.
            else if (request.method() == GET && request.uri().startsWith(ServerConstants.WEBSOCKET_PATH)) {
                loadContext(ctx, request);
            }
        } catch (Exception ex) {
            httpProcessor.sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
            log.error("error at process Http request", ex);
        }
        ctx.fireChannelRead(request);
    }

    public void loadContext(ChannelHandlerContext ctx, FullHttpRequest request) {
        Map<String, List<String>> requestParams = new QueryStringDecoder(request.uri()).parameters();
        if (requestParams.isEmpty() || requestParams.containsKey(ServerConstants.HTTP_PARAM_TOKEN)) {
            //String jsonMessage = new String(Base64.getDecoder().decode(requestParams.get(HTTP_PARAM_TOKEN).get(0)), StandardCharsets.UTF_8);
            final String jwt = requestParams.get(ServerConstants.HTTP_PARAM_TOKEN).get(0);
            final String userEmail;
            userEmail = jwtService.extractUsername(jwt);
            if(userEmail != null && !UserContext.hasContext(ctx.channel())){
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
                if(jwtService.isTokenValid(jwt, userDetails)){
                    UserContext.setContext(ctx.channel(), new Session((UserDto)userDetails));
                }
            }
        }
    }
}
