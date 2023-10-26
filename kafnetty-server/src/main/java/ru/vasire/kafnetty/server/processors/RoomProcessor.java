package ru.vasire.kafnetty.server.processors;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.vasire.kafnetty.server.dto.*;
import ru.vasire.kafnetty.server.repository.RoomRepository;
import ru.vasire.kafnetty.server.entity.Room;
import ru.vasire.kafnetty.server.mapper.RoomMapper;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public final class RoomProcessor {
    private final RoomRepository roomRepository;

    public RoomDto processMessage(BaseDto message, Channel channel) {
        RoomDto roomDto = (RoomDto)message;
        Room room = RoomMapper.INSTANCE.RoomDtoToRoom(roomDto);
        if (room == null)
            throw new RuntimeException("ChatMessage is not valid");
        room = roomRepository.saveAndFlush(room);
        return RoomMapper.INSTANCE.RoomToRoomDto(room);
    }
    public RoomListDto getRoomList(UUID clientId){
         RoomListDto roomListDto = new RoomListDto();
         roomListDto.setRooms(roomRepository.findAll().stream().map(RoomMapper.INSTANCE::RoomToRoomDto).toList());
         return roomListDto;
    }
}
