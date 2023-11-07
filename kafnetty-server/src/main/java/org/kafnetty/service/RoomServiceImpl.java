package org.kafnetty.service;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kafnetty.dto.channel.ChannelRoomDto;
import org.kafnetty.dto.channel.ChannelRoomListDto;
import org.kafnetty.entity.Message;
import org.kafnetty.entity.Room;
import org.kafnetty.kafka.config.KafnettyKafkaConfig;
import org.kafnetty.mapper.RoomMapper;
import org.kafnetty.repository.RoomRepository;
import org.kafnetty.type.OPERATION_TYPE;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {
    private final KafnettyKafkaConfig kafnettyKafkaConfig;
    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    @Override
    public ChannelRoomDto processLocalMessage(ChannelRoomDto message, Channel channel) {
        message.setClusterId(kafnettyKafkaConfig.getGroupId());
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
            room.setSent(!kafnettyKafkaConfig.getGroupId().equals(roomDto.getClusterId()));
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
    public void setRoomAsSended(ChannelRoomDto channelRoomDto) {
        Optional<Room> roomOptional = roomRepository.findById(channelRoomDto.getId());
        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            room.setSent(true);
            roomRepository.saveAndFlush(room);
        }
    }

    @Override
    public List<ChannelRoomDto> getNotSyncRooms() {
        List<Room> rooms = roomRepository.findAllByIsSentAndClusterId(false, kafnettyKafkaConfig.getGroupId());
        return roomMapper.mapToChannelRoomDtoList(rooms);
    }
}
