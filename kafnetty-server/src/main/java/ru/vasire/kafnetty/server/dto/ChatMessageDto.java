package ru.vasire.kafnetty.server.dto;

import lombok.Getter;
import lombok.Setter;
import ru.vasire.kafnetty.server.types.MESSAGE_TYPE;
import ru.vasire.kafnetty.server.types.OPERATION_TYPE;

import java.util.UUID;

@Getter
@Setter
public class ChatMessageDto extends BaseDto {
    private UUID id;
    private String messageText;
    private UUID senderId;
    private UUID roomId;
    private String sender;

    public ChatMessageDto(OPERATION_TYPE operationType, UUID roomId, UUID senderId, String messageText) {
        super(MESSAGE_TYPE.MESSAGE, operationType);
        this.messageText = messageText;
        this.roomId = roomId;
        this.senderId = senderId;
    }

    public ChatMessageDto() {
        super(MESSAGE_TYPE.MESSAGE, OPERATION_TYPE.CREATE);
    }
}
