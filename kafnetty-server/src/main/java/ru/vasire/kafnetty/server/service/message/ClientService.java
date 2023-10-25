package ru.vasire.kafnetty.server.service.message;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vasire.kafnetty.server.dto.ClientDto;
import ru.vasire.kafnetty.server.dto.UserProfileDto;
import ru.vasire.kafnetty.server.entity.Client;
import ru.vasire.kafnetty.server.mapper.UserProfileDtoMapper;
import ru.vasire.kafnetty.server.repository.ClientRepository;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public final class ClientService {
    private static final Map<String, UserProfileDto> USER_PROFILES = new ConcurrentHashMap<>();
    private final ClientRepository clientRepository;

    public Client clientLogin(String request, String channelLongId) {
        String json = new String(Base64.getDecoder().decode(request), StandardCharsets.UTF_8);
        ClientDto clientDto = ClientDto.encode(json, ClientDto.class);
        if (!checkToken(clientDto)) {
            // TODO обработать неверный вход
            throw new RuntimeException("User is not authorized");
        }
        Client client = clientRepository.findByLogin(clientDto.getLogin()).orElse(null);
        if (client == null) {
            // TODO обработать неверный вход
            throw new RuntimeException("User is not authorized");
        }
        client.setRoomId(clientDto.getRoomId());
        savePfofile(client, channelLongId);
        return client;
    }

    private void savePfofile(Client client, String channelLongId) {
        if (channelLongId != null && client != null) {
            USER_PROFILES.put(channelLongId, UserProfileDtoMapper.INSTANCE.ClientToUserProfileDto(client));
        }
    }
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
}


