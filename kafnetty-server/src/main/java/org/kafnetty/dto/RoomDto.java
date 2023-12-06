package org.kafnetty.dto;

import lombok.Getter;
import lombok.Setter;
import org.kafnetty.type.MessageType;
import org.kafnetty.type.OperationType;

@Getter
@Setter
public class RoomDto extends BaseDto {
    private String name;
    private long messageCount;

    public RoomDto() {
        super(MessageType.ROOM, OperationType.UPDATE);
    }
}
