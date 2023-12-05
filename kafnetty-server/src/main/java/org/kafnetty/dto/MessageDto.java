package org.kafnetty.dto;

import lombok.Getter;
import lombok.Setter;
import org.kafnetty.type.MessageType;
import org.kafnetty.type.OperationType;

import java.util.UUID;

@Getter
@Setter
public class MessageDto extends BaseDto {
    private String messageText;
    private UUID senderId;
    private UUID roomId;
    private String sender;
    private String clusterId;

    public MessageDto() {
        super(MessageType.MESSAGE, OperationType.CREATE);
    }
}
