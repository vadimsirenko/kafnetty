package org.kafnetty.mapper;

import org.kafnetty.dto.channel.ChannelMessageDto;
import org.kafnetty.dto.channel.ChannelRoomDto;
import org.kafnetty.dto.kafka.KafkaRoomDto;
import org.kafnetty.entity.Message;
import org.kafnetty.entity.Room;
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
    ChannelRoomDto RoomToChannelRoomDto(Room room);

    @Mapping(ignore = true, target = "sent")
    Room ChannelRoomDtoToRoom(ChannelRoomDto channelRoomDto);
    List<ChannelRoomDto> mapToChannelRoomDtoList(List<Room> clients);
    @Mapping(ignore = true, target = "kafkaMessageId")
    @Mapping(ignore = true, target = "clusterId")
    KafkaRoomDto ChannelRoomDtoToKafkaRoomDto(ChannelRoomDto channelRoomDto);

    ChannelRoomDto KafkaRoomDtoToChannelRoomDto(KafkaRoomDto kafkaRoomDto);
}

