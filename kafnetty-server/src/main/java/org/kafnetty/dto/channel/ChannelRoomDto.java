package org.kafnetty.dto.channel;

import lombok.Getter;
import lombok.Setter;
import org.kafnetty.type.MESSAGE_TYPE;
import org.kafnetty.type.OPERATION_TYPE;

import java.util.UUID;

@Getter
@Setter
public class ChannelRoomDto extends ChannelBaseDto {
    private UUID id;
    private String name;
    private long messageCount;

    public ChannelRoomDto(OPERATION_TYPE operationType, UUID id, String name) {
        super(MESSAGE_TYPE.ROOM, operationType);
        this.id = id;
        this.name = name;
    }

    public ChannelRoomDto() {
        super(MESSAGE_TYPE.ROOM, OPERATION_TYPE.UPDATE);
    }
}
