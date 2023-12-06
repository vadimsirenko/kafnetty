package org.kafnetty.service.chat;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kafnetty.dto.UserDto;
import org.kafnetty.store.entity.User;
import org.kafnetty.mapper.UserMapper;
import org.kafnetty.store.repository.UserRepository;
import org.kafnetty.type.OperationType;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientServiceImpl implements ClientService {
    private static final Map<String, UserDto> CHANNEL_USERS = new ConcurrentHashMap<>();
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private static boolean checkToken(UserDto req) {
        return true;
    }

    @Override
    public UserDto getChannelUser(String channelLongId) {
        if (channelLongId != null) {
            return CHANNEL_USERS.get(channelLongId);
        }
        return null;
    }

    @Override
    public boolean existsChannelUser(String channelLongId) {
        return CHANNEL_USERS.containsKey(channelLongId);
    }

    @Override
    public void setRoomForChannelUser(UUID roomId, String channelLongId) {
        if (CHANNEL_USERS.containsKey(channelLongId)) {
            UserDto userDto = CHANNEL_USERS.get(channelLongId);
            //userDto.setRoomId(roomId);
            CHANNEL_USERS.put(channelLongId, userDto);
        }
    }

    @Override
    public void removeChannelUser(String channelLongId) {
        CHANNEL_USERS.remove(channelLongId);
    }

    @Override
    public UserDto processMessage(UserDto message, Channel channel, String groupId) {
        if (message == null) {
            throw new RuntimeException("User is not authorized");
        }
        if (!checkToken(message)) {
            // TODO обработать неверный вход
            throw new RuntimeException("User is not authorized");
        }
        User user;
        if (message.getOperationType() == OperationType.LOGON) {
            user = userRepository.findByEmail(message.getEmail()).orElse(null);
            if (user == null) {
                // TODO обработать неверный вход
                throw new RuntimeException("User is not authorized");
            }
        } else {
            user = userRepository.findById(message.getId()).orElse(null);
            if (user == null) {
                // TODO обработать неверный вход
                throw new RuntimeException("User is not authorized");
            }
            user.setEmail(message.getEmail());
            user.setSent(!groupId.equals(message.getClusterId()));
            user = userRepository.saveAndFlush(user);
        }
        if (channel != null) {
            CHANNEL_USERS.put(channel.id().asLongText(), userMapper.UserToUserDto(user));
        }
        UserDto result = userMapper.UserToUserDto(user);
        result.setOperationType(message.getOperationType());
        return result;
    }

    @Override
    public void setClientAsSent(UserDto channelUserDto) {
        Optional<User> clientOptional = userRepository.findById(channelUserDto.getId());
        if (clientOptional.isPresent()) {
            User user = clientOptional.get();
            user.setSent(true);
            userRepository.saveAndFlush(user);
        }
    }

    @Override
    public List<UserDto> getNotSyncClients(String groupId) {
        List<User> users = userRepository.findBySent(false);
        return userMapper.ToUserDtoList(users);
    }
}


