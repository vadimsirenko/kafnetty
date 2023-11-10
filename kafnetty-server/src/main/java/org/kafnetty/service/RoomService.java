package org.kafnetty.service;

import org.kafnetty.dto.RoomDto;
import org.kafnetty.dto.RoomListDto;

import java.util.List;
import java.util.UUID;

public interface RoomService {
    RoomDto processMessage(RoomDto message);

    RoomListDto getRoomList(UUID clientId);

    void setRoomAsSent(RoomDto channelRoomDto);

    List<RoomDto> getNotSyncRooms();
}
