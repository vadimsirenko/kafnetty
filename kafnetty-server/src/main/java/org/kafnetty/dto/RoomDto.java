package org.kafnetty.dto;

import lombok.Getter;
import lombok.Setter;
import org.kafnetty.type.MESSAGE_TYPE;
import org.kafnetty.type.OPERATION_TYPE;

@Getter
@Setter
public class RoomDto extends BaseDto {
    private String name;
    private long messageCount;
    private String clusterId;

    public RoomDto() {
        super(MESSAGE_TYPE.ROOM, OPERATION_TYPE.UPDATE);
    }
}
