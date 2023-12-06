package org.kafnetty.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.kafnetty.type.MessageType;
import org.kafnetty.type.OperationType;

import java.util.List;

@Getter
@Setter
public class RoomListDto extends BaseDto {
    @JsonProperty("rooms")
    private List<RoomDto> rooms;

    public RoomListDto() {
        super(MessageType.ROOM_LIST, OperationType.UPDATE);
    }
}
