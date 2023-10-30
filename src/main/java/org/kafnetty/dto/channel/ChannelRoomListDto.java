package org.kafnetty.dto.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.kafnetty.type.MESSAGE_TYPE;
import org.kafnetty.type.OPERATION_TYPE;

import java.util.List;

@Getter
@Setter
public class ChannelRoomListDto extends ChannelBaseDto {
    @JsonProperty("rooms")
    private List<ChannelRoomDto> rooms;

    public ChannelRoomListDto(OPERATION_TYPE operationType, List<ChannelRoomDto> rooms) {
        super(MESSAGE_TYPE.ROOM_LIST, operationType);
        this.rooms = rooms;
    }

    public ChannelRoomListDto() {
        super(MESSAGE_TYPE.ROOM_LIST, OPERATION_TYPE.UPDATE);
    }
}
