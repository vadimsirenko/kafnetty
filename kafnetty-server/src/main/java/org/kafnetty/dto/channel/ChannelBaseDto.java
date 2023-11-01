package org.kafnetty.dto.channel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.kafnetty.type.MESSAGE_TYPE;
import org.kafnetty.type.OPERATION_TYPE;

@Getter
@Setter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "messageType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ChannelMessageDto.class, name = "MESSAGE"),
        @JsonSubTypes.Type(value = ChannelMessageListDto.class, name = "MESSAGE_LIST"),
        @JsonSubTypes.Type(value = ChannelRoomDto.class, name = "ROOM"),
        @JsonSubTypes.Type(value = ChannelRoomListDto.class, name = "ROOM_LIST"),
        @JsonSubTypes.Type(value = ChannelClientDto.class, name = "CLIENT"),
        @JsonSubTypes.Type(value = ChannelErrorDto.class, name = "ERROR"),
        @JsonSubTypes.Type(value = ChannelInfoDto.class, name = "INFO")
})
public abstract class ChannelBaseDto<T extends ChannelBaseDto> {
    private static ObjectMapper MAPPER = new ObjectMapper();
    private MESSAGE_TYPE messageType;
    private OPERATION_TYPE operationType = OPERATION_TYPE.NONE;
    private Long ts;

    public ChannelBaseDto(MESSAGE_TYPE messageType, OPERATION_TYPE operationType) {
        this.messageType = messageType;
        this.operationType = operationType;
        this.ts = System.currentTimeMillis();
    }

    public ChannelBaseDto() {
        this.messageType = MESSAGE_TYPE.UNKNOWN;
        this.operationType = OPERATION_TYPE.NONE;
        this.ts = System.currentTimeMillis();
    }

    public static ChannelBaseDto decode(String jsonMessage) {
        try {
            return MAPPER.readValue(jsonMessage, ChannelBaseDto.class);
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

    public ChannelGroupFuture writeAndFlush(ChannelGroup channelGroup) {
        return channelGroup.writeAndFlush(new TextWebSocketFrame(this.toJson()));
    }

    public ChannelFuture writeAndFlush(Channel channel) {
        return channel.writeAndFlush(new TextWebSocketFrame(this.toJson()));
    }
}
