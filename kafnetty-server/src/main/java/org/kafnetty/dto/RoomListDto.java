package org.kafnetty.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.kafnetty.type.MESSAGE_TYPE;
import org.kafnetty.type.OPERATION_TYPE;

import java.util.List;

@Getter
@Setter
public class RoomListDto extends BaseDto {
    @JsonProperty("rooms")
    private List<RoomDto> rooms;

    public RoomListDto() {
        super(MESSAGE_TYPE.ROOM_LIST, OPERATION_TYPE.UPDATE);
    }
}
