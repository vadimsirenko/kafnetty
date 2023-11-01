package org.kafnetty.service;

import io.netty.channel.Channel;
import org.kafnetty.dto.channel.ChannelBaseDto;
import org.kafnetty.dto.channel.ChannelRoomDto;
import org.kafnetty.dto.channel.ChannelRoomListDto;
import org.kafnetty.entity.Room;
import org.kafnetty.mapper.RoomMapper;
import org.kafnetty.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RoomServiceImpl implements RoomService {
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private RoomMapper roomMapper;

    @Override
    public ChannelRoomDto processMessage(ChannelBaseDto message, Channel channel) {
        ChannelRoomDto roomDto = (ChannelRoomDto) message;
        Room room = roomMapper.ChannelRoomDtoToRoom(roomDto);
        if (room == null)
            throw new RuntimeException("Message is not valid");
        room = roomRepository.saveAndFlush(room);
        return roomMapper.RoomToChannelRoomDto(room);
    }
    @Override
    public ChannelRoomListDto getRoomList(UUID clientId) {
        ChannelRoomListDto roomListDto = new ChannelRoomListDto();
        roomListDto.setRooms(roomRepository.findAll().stream().map(roomMapper::RoomToChannelRoomDto).toList());
        return roomListDto;
    }
}
