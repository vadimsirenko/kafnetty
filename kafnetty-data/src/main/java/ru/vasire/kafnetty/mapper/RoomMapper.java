package ru.vasire.kafnetty.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import ru.vasire.kafnetty.dto.RoomDto;
import ru.vasire.kafnetty.entity.Room;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoomMapper {
    RoomMapper INSTANCE = Mappers.getMapper( RoomMapper.class );
    @Mapping(ignore = true, target = "messageType")
    @Mapping(ignore = true, target = "operationType")
    @Mapping(ignore = true, target = "ts")
    RoomDto RoomToRoomDto(Room room);

    Room RoomDtoToRoom(RoomDto roomDto);
}

