package org.kafnetty.service;

import io.netty.channel.Channel;
import org.kafnetty.dto.channel.ChannelRoomDto;
import org.kafnetty.dto.channel.ChannelRoomListDto;

import java.util.List;
import java.util.UUID;

public interface RoomService {
    ChannelRoomDto processMessage(ChannelRoomDto message);

    ChannelRoomDto processLocalMessage(ChannelRoomDto message, Channel channel);

    ChannelRoomListDto getRoomList(UUID clientId);

    void setRoomAsSended(ChannelRoomDto channelRoomDto);
    List<ChannelRoomDto> getNotSyncRooms();
}
