package ru.vasire.kafnetty.dto;

import lombok.Getter;
import lombok.Setter;
import ru.vasire.kafnetty.types.MESSAGE_TYPE;
import ru.vasire.kafnetty.types.OPERATION_TYPE;

import java.util.UUID;

@Getter
@Setter
public class RoomDto extends BaseDto {
    private UUID id;
    private String name;
    private long messageCount;

    public RoomDto(OPERATION_TYPE operationType, UUID id, String name) {
        super(MESSAGE_TYPE.ROOM, operationType);
        this.id = id;
        this.name = name;
    }
    public RoomDto() {
        super(MESSAGE_TYPE.ROOM, OPERATION_TYPE.UPDATE);
    }
}
