package org.kafnetty.service;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import org.kafnetty.dto.UserProfileDto;
import org.kafnetty.dto.channel.ChannelBaseDto;
import org.kafnetty.dto.channel.ChannelClientDto;
import org.kafnetty.entity.Client;
import org.kafnetty.mapper.ClientMapper;
import org.kafnetty.mapper.UserProfileDtoMapper;
import org.kafnetty.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ClientServiceImpl implements ClientService {

    private static final Map<String, UserProfileDto> USER_PROFILES = new ConcurrentHashMap<>();
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private UserProfileDtoMapper userProfileDtoMapper;
    @Autowired
    private ClientMapper clientMapper;

    private static boolean checkToken(ChannelClientDto req) {
        return true;
    }

    @Override
    public UserProfileDto getProfile(String channelLongId) {
        if (channelLongId != null) {
            return USER_PROFILES.get(channelLongId);
        }
        return null;
    }
    @Override
    public boolean existsUserProfile(String channelLongId) {
        return USER_PROFILES.containsKey(channelLongId);
    }
    @Override
    public void setRoomForUserProfile(UUID roomId, String channelLongId) {
        if (USER_PROFILES.containsKey(channelLongId)) {
            UserProfileDto userProfileDto = USER_PROFILES.get(channelLongId);
            userProfileDto.setRoomId(roomId);
            USER_PROFILES.put(channelLongId, userProfileDto);
        }
    }
    @Override
    public void removeProfile(String channelLongId) {
        if (USER_PROFILES.containsKey(channelLongId)) {
            USER_PROFILES.remove(channelLongId);
        }
    }
    @Override
    public ChannelClientDto processMessage(ChannelBaseDto message, Channel channel) {
        ChannelClientDto clientDto = (ChannelClientDto) message;
        if (clientDto.getRoomId() == null) {
            throw new RuntimeException("User is not authorized");
        }
        if (!checkToken((ChannelClientDto) clientDto)) {
            // TODO обработать неверный вход
            throw new RuntimeException("User is not authorized");
        }
        Client client = clientRepository.findByLogin(clientDto.getLogin()).orElse(null);
        if (client == null) {
            // TODO обработать неверный вход
            throw new RuntimeException("User is not authorized");
        }
        client.setRoomId(clientDto.getRoomId());
        USER_PROFILES.put(channel.id().asLongText(), userProfileDtoMapper.ClientToUserProfileDto(client));
        return clientMapper.ClientToChannelClientDto(client);
    }
}


