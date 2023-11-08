package org.kafnetty.dto;

import lombok.Getter;
import lombok.Setter;
import org.kafnetty.type.MESSAGE_TYPE;
import org.kafnetty.type.OPERATION_TYPE;

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
        super(MESSAGE_TYPE.MESSAGE, OPERATION_TYPE.CREATE);
    }
}
