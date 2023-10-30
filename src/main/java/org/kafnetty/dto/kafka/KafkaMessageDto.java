package org.kafnetty.dto.kafka;

import lombok.Getter;
import lombok.Setter;
import org.kafnetty.type.MESSAGE_TYPE;
import org.kafnetty.type.OPERATION_TYPE;

import java.util.UUID;

@Getter
@Setter
public class KafkaMessageDto extends KafkaBaseDto {
    private UUID id;
    private String messageText;
    private UUID senderId;
    private UUID roomId;
    private String sender;

    public KafkaMessageDto(OPERATION_TYPE operationType, UUID roomId, UUID senderId, String messageText) {
        super(MESSAGE_TYPE.MESSAGE, operationType);
        this.messageText = messageText;
        this.roomId = roomId;
        this.senderId = senderId;
    }

    public KafkaMessageDto() {
        super(MESSAGE_TYPE.MESSAGE, OPERATION_TYPE.CREATE);
    }
}
