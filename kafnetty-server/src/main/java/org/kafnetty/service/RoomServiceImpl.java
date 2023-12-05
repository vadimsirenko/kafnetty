package org.kafnetty.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kafnetty.dto.RoomDto;
import org.kafnetty.dto.RoomListDto;
import org.kafnetty.entity.Room;
import org.kafnetty.kafka.config.KafnettyConsumerConfig;
import org.kafnetty.mapper.RoomMapper;
import org.kafnetty.repository.RoomRepository;
import org.kafnetty.type.OperationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {
    @Autowired
    private KafnettyConsumerConfig kafnettyConsumerConfig;
    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    @Override
    public RoomDto processMessage(RoomDto roomDto) {
        Room room = roomMapper.RoomDtoToRoom(roomDto);
        if (room == null)
            throw new RuntimeException("Message is not valid");
        Room existsRoom = roomRepository.findByName(room.getName());
        if (existsRoom != null && roomDto.getOperationType() == OperationType.CREATE) {
            room = existsRoom;
        } else if (existsRoom != null && roomDto.getOperationType() == OperationType.UPDATE) {
            existsRoom.setName(roomDto.getName());
            room = roomRepository.saveAndFlush(existsRoom);
        } else {
            room.setSent(!kafnettyConsumerConfig.getGroupId().equals(roomDto.getClusterId()));
            room = roomRepository.saveAndFlush(room);
        }
        return roomMapper.RoomToChannelRoomDto(room);
    }

    @Override
    public RoomListDto getRoomList(UUID clientId) {
        RoomListDto roomListDto = new RoomListDto();
        roomListDto.setRooms(roomRepository.findAll().stream().map(roomMapper::RoomToChannelRoomDto).toList());
        return roomListDto;
    }

    @Override
    public void setRoomAsSent(RoomDto channelRoomDto) {
        Optional<Room> roomOptional = roomRepository.findById(channelRoomDto.getId());
        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            room.setSent(true);
            roomRepository.saveAndFlush(room);
        }
    }

    @Override
    public List<RoomDto> getNotSyncRooms() {
        List<Room> rooms = roomRepository.findBySentAndClusterId(false, kafnettyConsumerConfig.getGroupId());
        return roomMapper.ToRoomDtoList(rooms);
    }
}
