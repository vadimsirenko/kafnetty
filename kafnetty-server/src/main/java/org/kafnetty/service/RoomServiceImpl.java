package org.kafnetty.service;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import org.kafnetty.dto.channel.ChannelRoomDto;
import org.kafnetty.dto.channel.ChannelRoomListDto;
import org.kafnetty.entity.Room;
import org.kafnetty.kafka.producer.KafnettyProducer;
import org.kafnetty.mapper.RoomMapper;
import org.kafnetty.repository.RoomRepository;
import org.kafnetty.type.OPERATION_TYPE;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    private final KafnettyProducer kafkaProducer;
    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    @Override
    public ChannelRoomDto processLocalMessage(ChannelRoomDto message, Channel channel) {
        message.setClusterId(kafkaProducer.getGroupId());
        return processMessage(message);
    }

    @Override
    public ChannelRoomDto processMessage(ChannelRoomDto roomDto) {
        Room room = roomMapper.ChannelRoomDtoToRoom(roomDto);
        if (room == null)
            throw new RuntimeException("Message is not valid");
        Room existsRoom = roomRepository.findByName(room.getName());
        if (existsRoom != null && roomDto.getOperationType() == OPERATION_TYPE.CREATE) {
            room = existsRoom;
        } else if (existsRoom != null && roomDto.getOperationType() == OPERATION_TYPE.UPDATE) {
            existsRoom.setName(roomDto.getName());
            room = roomRepository.saveAndFlush(existsRoom);
        } else {
            room = roomRepository.saveAndFlush(room);
        }
        return roomMapper.RoomToChannelRoomDto(room);
    }

    @Override
    public ChannelRoomListDto getRoomList(UUID clientId) {
        ChannelRoomListDto roomListDto = new ChannelRoomListDto();
        roomListDto.setRooms(roomRepository.findAll().stream().map(roomMapper::RoomToChannelRoomDto).toList());
        return roomListDto;
    }

    @Override
    public void setMessageAsSended(ChannelRoomDto channelRoomDto) {
        Optional<Room> roomOptional = roomRepository.findById(channelRoomDto.getId());
        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            room.setSent(true);
            roomRepository.saveAndFlush(room);
        }
    }
}
