package ru.vasire.kafnetty.server.processors;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.vasire.kafnetty.server.dto.BaseDto;
import ru.vasire.kafnetty.server.dto.ClientDto;
import ru.vasire.kafnetty.server.dto.UserProfileDto;
import ru.vasire.kafnetty.server.entity.Client;
import ru.vasire.kafnetty.server.mapper.ClientMapper;
import ru.vasire.kafnetty.server.mapper.UserProfileDtoMapper;
import ru.vasire.kafnetty.server.repository.ClientRepository;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public final class ClientPoocessor {
    private static final Map<String, UserProfileDto> USER_PROFILES = new ConcurrentHashMap<>();
    private final ClientRepository clientRepository;

    public UserProfileDto getProfile(String channelLongId) {
        if (channelLongId != null) {
            return USER_PROFILES.get(channelLongId);
        }
        return null;
    }
    public boolean existsUserProfile(String channelLongId){
        return USER_PROFILES.containsKey(channelLongId);
    }

    private static boolean checkToken(ClientDto req) {
        return true;
    }

    public void setRoomForUserProfile(UUID roomId, String channelLongId) {
        if(USER_PROFILES.containsKey(channelLongId)){
            UserProfileDto userProfileDto = USER_PROFILES.get(channelLongId);
            userProfileDto.setRoomId(roomId);
            USER_PROFILES.put(channelLongId, userProfileDto);
        }
    }

    public void removeProfile(String channelLongId) {
        if(USER_PROFILES.containsKey(channelLongId)){
            USER_PROFILES.remove(channelLongId);
        }
    }

    public ClientDto processMessage(BaseDto message, Channel channel) {
        ClientDto clientDto = (ClientDto)message;
        if (clientDto.getRoomId() == null) {
            throw new RuntimeException("User is not authorized");
        }
        if (!checkToken((ClientDto) clientDto)) {
            // TODO обработать неверный вход
            throw new RuntimeException("User is not authorized");
        }
        Client client = clientRepository.findByLogin(clientDto.getLogin()).orElse(null);
        if (client == null) {
            // TODO обработать неверный вход
            throw new RuntimeException("User is not authorized");
        }
        client.setRoomId(clientDto.getRoomId());
        USER_PROFILES.put(channel.id().asLongText(), UserProfileDtoMapper.INSTANCE.ClientToUserProfileDto(client));
        return ClientMapper.INSTANCE.ClientToClientDto(client);
    }
}


