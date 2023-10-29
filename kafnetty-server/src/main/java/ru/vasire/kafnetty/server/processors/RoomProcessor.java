package ru.vasire.kafnetty.server.processors;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.vasire.kafnetty.dto.BaseDto;
import ru.vasire.kafnetty.dto.RoomDto;
import ru.vasire.kafnetty.dto.RoomListDto;
import ru.vasire.kafnetty.entity.Room;
import ru.vasire.kafnetty.mapper.RoomMapper;
import ru.vasire.kafnetty.server.repository.RoomRepository;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RoomProcessor {
    @Autowired
    private RoomMapper roomMapper;

    private final RoomRepository roomRepository;

    public RoomDto processMessage(BaseDto message, Channel channel) {
        RoomDto roomDto = (RoomDto)message;
        Room room = roomMapper.RoomDtoToRoom(roomDto);
        if (room == null)
            throw new RuntimeException("ChatMessage is not valid");
        room = roomRepository.saveAndFlush(room);
        return roomMapper.RoomToRoomDto(room);
    }
    public RoomListDto getRoomList(UUID clientId){
         RoomListDto roomListDto = new RoomListDto();
         roomListDto.setRooms(roomRepository.findAll().stream().map(roomMapper::RoomToRoomDto).toList());
         return roomListDto;
    }
}
