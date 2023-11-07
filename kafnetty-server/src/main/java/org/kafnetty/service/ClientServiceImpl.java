package org.kafnetty.service;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kafnetty.dto.UserProfileDto;
import org.kafnetty.dto.channel.ChannelClientDto;
import org.kafnetty.entity.Client;
import org.kafnetty.kafka.config.KafnettyKafkaConfig;
import org.kafnetty.mapper.ClientMapper;
import org.kafnetty.mapper.UserProfileDtoMapper;
import org.kafnetty.repository.ClientRepository;
import org.kafnetty.type.OPERATION_TYPE;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientServiceImpl implements ClientService {
    private static final Map<String, UserProfileDto> USER_PROFILES = new ConcurrentHashMap<>();
    private final ClientRepository clientRepository;
    private final UserProfileDtoMapper userProfileDtoMapper;
    private final ClientMapper clientMapper;
    private final KafnettyKafkaConfig kafnettyKafkaConfig;

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
        USER_PROFILES.remove(channelLongId);
    }

    @Override
    public ChannelClientDto processLocalMessage(ChannelClientDto message, Channel channel) {
        message.setClusterId(kafnettyKafkaConfig.getGroupId());
        return processMessage(message, channel);
    }

    @Override
    public ChannelClientDto processMessage(ChannelClientDto message, Channel channel) {
        if (message == null) {
            throw new RuntimeException("User is not authorized");
        }
        if (!checkToken(message)) {
            // TODO обработать неверный вход
            throw new RuntimeException("User is not authorized");
        }
        Client client;
        if (message.getOperationType() == OPERATION_TYPE.LOGON) {
            client = clientRepository.findByLogin(message.getLogin()).orElse(null);
            if (client == null) {
                // TODO обработать неверный вход
                throw new RuntimeException("User is not authorized");
            }
        } else {
            client = clientRepository.findById(message.getId()).orElse(null);
            if (client == null) {
                // TODO обработать неверный вход
                throw new RuntimeException("User is not authorized");
            }
            client.setLogin(message.getLogin());
            client.setNickName(message.getNickName());
            client.setEmail(message.getEmail());
            client.setEmail(message.getEmail());
            client.setTs(new Date().getTime());
            client.setSent(!kafnettyKafkaConfig.getGroupId().equals(message.getClusterId()));
            client = clientRepository.saveAndFlush(client);
        }
        client.setRoomId(message.getRoomId());
        if (channel != null) {
            USER_PROFILES.put(channel.id().asLongText(), userProfileDtoMapper.ClientToUserProfileDto(client));
        }
        ChannelClientDto result = clientMapper.ClientToChannelClientDto(client);
        result.setOperationType(message.getOperationType());
        return result;
    }

    @Override
    public void setClientAsSended(ChannelClientDto channelClientDto) {
        Optional<Client> clientOptional = clientRepository.findById(channelClientDto.getId());
        if (clientOptional.isPresent()) {
            Client client = clientOptional.get();
            client.setSent(true);
            clientRepository.saveAndFlush(client);
        }
    }
    @Override
    public List<ChannelClientDto> getNotSyncClients(){
        List<Client> clients = clientRepository.findAllByIsSentAndClusterId(false, kafnettyKafkaConfig.getGroupId());
        return clientMapper.mapToChannelClientDtoList(clients);
    }
}


