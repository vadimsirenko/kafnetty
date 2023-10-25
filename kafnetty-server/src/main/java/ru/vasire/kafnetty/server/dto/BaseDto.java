package ru.vasire.kafnetty.server.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseDto {
    private MESSAGE_TYPE messageType;
    private OPERATION_TYPE operationType;
    private Long ts;

    public BaseDto(MESSAGE_TYPE messageType, OPERATION_TYPE operationType) {
        this.messageType = messageType;
        this.operationType = operationType;
        this.ts = System.currentTimeMillis();
    }

    public BaseDto() {
        this.messageType = MESSAGE_TYPE.UNKNOWN;
        this.operationType = OPERATION_TYPE.NONE;
        this.ts = System.currentTimeMillis();
    }
    public WebSocketFrame toWebSocketFrame(){
        try {
            String messageJson = new ObjectMapper().writeValueAsString(this);
            return new TextWebSocketFrame(messageJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public static <T> T encode(String jsonMessage, Class<T> clazz){
        try {
            return new ObjectMapper().readValue(jsonMessage, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
