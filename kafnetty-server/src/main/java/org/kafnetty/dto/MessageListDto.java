package org.kafnetty.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.kafnetty.type.MESSAGE_TYPE;
import org.kafnetty.type.OPERATION_TYPE;

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
        super(MESSAGE_TYPE.MESSAGE_LIST, OPERATION_TYPE.UPDATE);
    }

    public MessageListDto(UUID roomId, UUID senderId) {
        this();
        this.roomId = roomId;
        this.senderId = senderId;
    }
}
