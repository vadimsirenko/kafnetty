package org.kafnetty.mapper;

import org.kafnetty.dto.channel.ChannelRoomDto;
import org.kafnetty.entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoomMapper {
    RoomMapper INSTANCE = Mappers.getMapper(RoomMapper.class);

    @Mapping(ignore = true, target = "messageType")
    @Mapping(ignore = true, target = "operationType")
    @Mapping(ignore = true, target = "ts")
    ChannelRoomDto RoomToChannelRoomDto(Room room);

    Room ChannelRoomDtoToRoom(ChannelRoomDto channelRoomDto);
}

