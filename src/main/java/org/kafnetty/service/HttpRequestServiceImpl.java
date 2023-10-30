package org.kafnetty.service;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import org.kafnetty.dto.channel.ChannelErrorDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@Component
//@RequiredArgsConstructor
public class HttpRequestServiceImpl implements HttpRequestService {

    @Autowired
    private ChatService chatService;
    @Value("${server.web-socket-path}")
    private String webSocketPath;
    @Value("${server.static-path}")
    private String staticPath;
    @Value("${server.chat-path}")
    private String chatPath;
    @Autowired
    private ContentTypeResolverService contentTypeResolverService;

    @Override
    public void processWebSocketRequest(Channel channel, WebSocketFrame frame) {
        try {
            if (!chatService.existsUserProfile(channel)) {
                ChannelErrorDto.createCommonError("You can't chat without logging in").writeAndFlush(channel);
            } else {
                chatService.processMessage(((TextWebSocketFrame) frame).text(), channel);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean processHttpRequest(ChannelHandlerContext ctx, HttpRequest request) {
        try {
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

            if (request.uri().startsWith(staticPath) || request.uri().startsWith(chatPath)) {
                handleResource(ctx, request, request.uri().substring(1));
                return false;
            }
            if (!request.uri().startsWith(webSocketPath)) {
                handleResource(ctx, request, request.uri().substring(1));
                return false;
            }
            Map<String, List<String>> requestParams = new QueryStringDecoder(request.uri()).parameters();

            if (requestParams.isEmpty() || !requestParams.containsKey(HTTP_PARAM_REQUEST)) {
                //System.err.println(HTTP_PARAM_REQUEST + " parameters are not default");
                //sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
                handleResource(ctx, request, request.uri().substring(1));
                return false;
            }
            String jsonMessage = new String(Base64.getDecoder().decode(requestParams.get(HTTP_PARAM_REQUEST).get(0)), StandardCharsets.UTF_8);
            chatService.processMessage(jsonMessage, ctx.channel());
            return true;
        } catch (RuntimeException ex) {
            sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
            System.err.println(ex.getMessage());
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleResource(ChannelHandlerContext ctx, HttpRequest request, String resource) throws IOException {
        //String url = this.getClass().getResource("/").getPath() + resource;
        int questionIndex = resource.indexOf("?");
        if (questionIndex != -1) {
            resource = resource.substring(0, questionIndex);
        }

        String url = this.getClass().getClassLoader().getResource(resource).getPath();

        File file = new File(url);
        if (!file.exists()) {
            sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
            return;
        }
        if (file.isDirectory()) {
            sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
            return;
        }
        handleFile(ctx, request, file);
    }

    private void handleFile(ChannelHandlerContext ctx, HttpRequest request, File file) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        HttpHeaders headers = getContentTypeHeader(file);
        HttpResponse response = new DefaultHttpResponse(request.protocolVersion(), HttpResponseStatus.OK, headers);
        ctx.write(response);
        ctx.write(new DefaultFileRegion(raf.getChannel(), 0, raf.length()));
        ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        future.addListener(ChannelFutureListener.CLOSE);
    }

    private HttpHeaders getContentTypeHeader(File file) throws IOException {
        HttpHeaders headers = new DefaultHttpHeaders();

        String contentType = contentTypeResolverService.getMimeType(file);

        if (contentType.equals("text/plain")) {
            // Поскольку текст будет отображаться в браузере, он указан как кодирование UTF-8 здесь
            contentType = "text/plain;charset=utf-8";
        }
        headers.set(HttpHeaderNames.CONTENT_TYPE, contentType);
        return headers;
    }


    /*
            HttpResponse response = new DefaultHttpResponse(request.protocolVersion(), HttpResponseStatus.OK, headers);
        ctx.write(response);
        ctx.write(new DefaultFileRegion(raf.getChannel(), 0, raf.length()));
        ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        future.addListener(ChannelFutureListener.CLOSE);
     */

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

    @Override
    public void InitChannel(Channel channel) {
        chatService.InitChannel(channel);
    }

    @Override
    public void removeChannel(Channel channel) {
        chatService.removeChannel(channel);
    }
}
