package ru.vasire.kafnetty.server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.vasire.kafnetty.server.entity.Room;
import ru.vasire.kafnetty.server.dto.RoomDto;

@Mapper
public interface RoomMapper {
    RoomMapper INSTANCE = Mappers.getMapper( RoomMapper.class );
    @Mapping(ignore = true, target = "messageType")
    @Mapping(ignore = true, target = "operationType")
    @Mapping(ignore = true, target = "ts")
    RoomDto RoomToRoomDto(Room room);

    Room RoomDtoToRoom(RoomDto roomDto);
}

