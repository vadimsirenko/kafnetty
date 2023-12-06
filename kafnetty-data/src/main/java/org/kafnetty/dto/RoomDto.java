package org.kafnetty.dto;

import org.kafnetty.type.OperationType;
import lombok.Getter;
import lombok.Setter;
import org.kafnetty.type.MessageType;

@Getter
@Setter
public class RoomDto extends BaseDto {
    private String name;
    private long messageCount;

    public RoomDto() {
        super(MessageType.ROOM, OperationType.UPDATE);
    }
}
