package org.kafnetty.dto.kafka;

import lombok.Getter;
import lombok.Setter;
import org.kafnetty.type.MESSAGE_TYPE;
import org.kafnetty.type.OPERATION_TYPE;

import java.util.UUID;

@Setter
@Getter
public class KafkaRoomDto extends KafkaBaseDto {
    private UUID id;
    private String name;
    private long messageCount;
    private String clusterId;

    public KafkaRoomDto(OPERATION_TYPE operationType, UUID id, String name) {
        super(MESSAGE_TYPE.ROOM, operationType);
        this.id = id;
        this.name = name;
    }

    public KafkaRoomDto() {
        super(MESSAGE_TYPE.ROOM, OPERATION_TYPE.CREATE);
    }
}
