package org.kafnetty.service.chat;

import org.kafnetty.dto.RoomDto;
import org.kafnetty.dto.RoomListDto;

import java.util.List;
import java.util.UUID;

public interface RoomService {
    RoomDto processMessage(RoomDto message, String groupId);

    RoomListDto getRoomList(UUID clientId);

    void setRoomAsSent(RoomDto channelRoomDto);

    List<RoomDto> getNotSyncRooms(String groupId);
}
