package org.kafnetty.dto.channel;

import lombok.Getter;
import lombok.Setter;
import org.kafnetty.type.MESSAGE_TYPE;
import org.kafnetty.type.OPERATION_TYPE;

import java.util.UUID;

@Getter
@Setter
public class ChannelMessageDto extends ChannelBaseDto {
    private UUID id;
    private String messageText;
    private UUID senderId;
    private UUID roomId;
    private String sender;

    public ChannelMessageDto(OPERATION_TYPE operationType, UUID roomId, UUID senderId, String messageText) {
        super(MESSAGE_TYPE.MESSAGE, operationType);
        this.messageText = messageText;
        this.roomId = roomId;
        this.senderId = senderId;
    }

    public ChannelMessageDto() {
        super(MESSAGE_TYPE.MESSAGE, OPERATION_TYPE.CREATE);
    }
}
