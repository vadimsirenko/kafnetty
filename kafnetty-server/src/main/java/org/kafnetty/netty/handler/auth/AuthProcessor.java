package org.kafnetty.netty.handler.auth;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.UnsupportedFormatException;
import org.kafnetty.dto.BaseDto;
import org.kafnetty.dto.TokenDto;
import org.kafnetty.dto.UserDto;
import org.kafnetty.entity.User;
import org.kafnetty.mapper.UserMapper;
import org.kafnetty.service.AuthenticationService;
import org.kafnetty.type.OperationType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static org.kafnetty.config.ServerConstants.HTTP_PARAM_TOKEN;
import static org.kafnetty.netty.handler.auth.UserContext.hasContext;
import static org.kafnetty.netty.handler.auth.UserContext.setContext;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthProcessor {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final AuthenticationService authenticationService;
    public void loadContext(ChannelHandlerContext ctx, FullHttpRequest request) {
        Map<String, List<String>> requestParams = new QueryStringDecoder(request.uri()).parameters();
        if (requestParams.isEmpty() || requestParams.containsKey(HTTP_PARAM_TOKEN)) {
            //String jsonMessage = new String(Base64.getDecoder().decode(requestParams.get(HTTP_PARAM_TOKEN).get(0)), StandardCharsets.UTF_8);
            final String jwt = requestParams.get(HTTP_PARAM_TOKEN).get(0);
            final String userEmail;
            userEmail = jwtService.extractUsername(jwt);
            if(userEmail != null && !hasContext(ctx.channel())){
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
                if(jwtService.isTokenValid(jwt, userDetails)){
                    setContext(ctx.channel(), new Session((User)userDetails));
                }
            }
        }
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
