package ru.vasire.kafnetty.server.service.message;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vasire.kafnetty.server.repository.RoomRepository;
import ru.vasire.kafnetty.server.dto.RoomDto;
import ru.vasire.kafnetty.server.dto.RoomListDto;
import ru.vasire.kafnetty.server.entity.Room;
import ru.vasire.kafnetty.server.mapper.RoomMapper;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public final class RoomService{
    private final RoomRepository roomRepository;
    public RoomDto processRequest(String requestJson) {
        RoomDto roomDto = RoomDto.encode(requestJson, RoomDto.class);
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
