package org.kafnetty.dto;

import org.kafnetty.type.OperationType;
import lombok.Getter;
import lombok.Setter;
import org.kafnetty.type.MessageType;

import java.util.UUID;

@Getter
@Setter
public class MessageDto extends BaseDto {
    private String messageText;
    private UUID senderId;
    private UUID roomId;
    private String sender;

    public MessageDto() {
        super(MessageType.MESSAGE, OperationType.CREATE);
    }
}
