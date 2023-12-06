package org.kafnetty.mapper;

import org.kafnetty.dto.RoomDto;
import org.kafnetty.store.entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoomMapper {
    RoomMapper INSTANCE = Mappers.getMapper(RoomMapper.class);

    @Mapping(ignore = true, target = "messageType")
    @Mapping(ignore = true, target = "operationType")
    @Mapping(ignore = true, target = "ts")
    RoomDto RoomToChannelRoomDto(Room room);

    @Mapping(ignore = true, target = "sent")
    Room RoomDtoToRoom(RoomDto channelRoomDto);

    List<RoomDto> ToRoomDtoList(List<Room> clients);
}

