package org.kafnetty.dto.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.kafnetty.type.MESSAGE_TYPE;
import org.kafnetty.type.OPERATION_TYPE;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ChannelMessageListDto extends ChannelBaseDto {
    @JsonProperty("messages")
    private List<ChannelMessageDto> messages;
    private UUID roomId;
    private UUID senderId;

    public ChannelMessageListDto(OPERATION_TYPE operationType, UUID roomId, List<ChannelMessageDto> messages) {
        super(MESSAGE_TYPE.MESSAGE_LIST, operationType);
        this.roomId = roomId;
        this.messages = messages;
    }

    public ChannelMessageListDto() {
        super(MESSAGE_TYPE.MESSAGE_LIST, OPERATION_TYPE.UPDATE);
    }

    public ChannelMessageListDto(UUID roomId, UUID senderId) {
        this();
        this.roomId = roomId;
        this.senderId = senderId;
    }
}
