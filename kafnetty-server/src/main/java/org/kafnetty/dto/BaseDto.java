package org.kafnetty.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.kafnetty.type.MessageType;
import org.kafnetty.type.OperationType;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "messageType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MessageDto.class, name = "MESSAGE"),
        @JsonSubTypes.Type(value = MessageListDto.class, name = "MESSAGE_LIST"),
        @JsonSubTypes.Type(value = RoomDto.class, name = "ROOM"),
        @JsonSubTypes.Type(value = RoomListDto.class, name = "ROOM_LIST"),
        @JsonSubTypes.Type(value = UserDto.class, name = "USER"),
        @JsonSubTypes.Type(value = ErrorDto.class, name = "ERROR"),
        @JsonSubTypes.Type(value = InfoDto.class, name = "INFO")
})
public abstract class BaseDto {
    private static ObjectMapper MAPPER = new ObjectMapper();
    private UUID id = UUID.randomUUID();
    private String clusterId;
    private MessageType messageType;
    private OperationType operationType = OperationType.NONE;
    private Long ts;

    public BaseDto(MessageType messageType, OperationType operationType) {
        this.messageType = messageType;
        this.operationType = operationType;
        this.ts = System.currentTimeMillis();
    }

    public BaseDto() {
        this.messageType = MessageType.UNKNOWN;
        this.ts = System.currentTimeMillis();
    }

    public static BaseDto decode(String jsonMessage) {
        try {
            return MAPPER.readValue(jsonMessage, BaseDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeAndFlush(ChannelGroup channelGroup) {
        channelGroup.writeAndFlush(new TextWebSocketFrame(this.toJson()));
    }

    public ChannelFuture writeAndFlush(Channel channel) {
        return channel.writeAndFlush(new TextWebSocketFrame(this.toJson()));
    }
}
