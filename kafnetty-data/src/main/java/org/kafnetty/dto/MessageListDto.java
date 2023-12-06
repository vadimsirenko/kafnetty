package org.kafnetty.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.kafnetty.type.MessageType;
import org.kafnetty.type.OperationType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class MessageListDto extends BaseDto {
    @JsonProperty("messages")
    private List<MessageDto> messages;
    private UUID roomId;
    private UUID senderId;

    public MessageListDto() {
        super(MessageType.MESSAGE_LIST, OperationType.UPDATE);
    }

    public MessageListDto(UUID roomId, UUID senderId) {
        this();
        this.roomId = roomId;
        this.senderId = senderId;
    }
}
