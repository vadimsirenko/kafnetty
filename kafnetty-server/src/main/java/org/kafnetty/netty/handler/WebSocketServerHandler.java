package org.kafnetty.netty.handler;

import io.netty.channel.*;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.*;
import org.kafnetty.service.HttpRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static io.netty.handler.codec.http.HttpHeaderNames.HOST;

@Component
@ChannelHandler.Sharable
@Qualifier("webSocketServerHandler")
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {
    private static final String WEBSOCKET_PATH = "/websocket";
    @Autowired
    private HttpRequestService httpRequestService;
    private WebSocketServerHandshaker handshaker;

    private static String getWebSocketLocation(HttpRequest req) {
        String location = req.headers().get(HOST) + WEBSOCKET_PATH;
        return "ws://" + location;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpRequest)
            handleHttpRequest(ctx, (HttpRequest) msg);
        else if (msg instanceof WebSocketFrame)
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        else
            System.err.println("Unknown request type: " + msg.getClass().getName());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest request) {
        if (httpRequestService.processHttpRequest(ctx, request)) {
            // Handshake
            WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(request), null, true);
            handshaker = wsFactory.newHandshaker(request);
            if (handshaker == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            } else {
                ChannelFuture channelFuture = handshaker.handshake(ctx.channel(), request);
                // After the handshake is successful, the business logic
                if (channelFuture.isSuccess()) {
                    httpRequestService.InitChannel(ctx.channel());
                }
            }
        }
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            System.out.println(ctx.channel() + " closed");
        } else if (frame instanceof PingWebSocketFrame) // binary date
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
        else if (!(frame instanceof TextWebSocketFrame)) // text data
            throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
        else
            httpRequestService.processWebSocketRequest(ctx.channel(), frame);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("Received " + incoming.remoteAddress() + " handshake request");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        httpRequestService.removeChannel(ctx.channel());
    }
}
