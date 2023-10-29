package ru.vasire.kafnetty.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.vasire.kafnetty.types.MESSAGE_TYPE;
import ru.vasire.kafnetty.types.OPERATION_TYPE;

import java.util.List;
@Getter
@Setter
public class RoomListDto extends BaseDto {
    @JsonProperty("rooms")
    private List<RoomDto> rooms;

    public RoomListDto(OPERATION_TYPE operationType, List<RoomDto> rooms) {
        super(MESSAGE_TYPE.ROOM_LIST, operationType);
        this.rooms = rooms;
    }
    public RoomListDto() {
        super(MESSAGE_TYPE.ROOM_LIST, OPERATION_TYPE.UPDATE);
    }
}
