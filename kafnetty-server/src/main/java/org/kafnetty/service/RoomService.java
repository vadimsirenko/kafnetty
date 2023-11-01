package org.kafnetty.service;

import io.netty.channel.Channel;
import org.kafnetty.dto.channel.ChannelBaseDto;
import org.kafnetty.dto.channel.ChannelRoomDto;
import org.kafnetty.dto.channel.ChannelRoomListDto;

import java.util.UUID;

public interface RoomService {
    ChannelRoomDto processMessage(ChannelBaseDto message, Channel channel);

    ChannelRoomListDto getRoomList(UUID clientId);
}
