package org.kafnetty.service;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kafnetty.dto.ClientDto;
import org.kafnetty.entity.Client;
import org.kafnetty.kafka.config.KafnettyKafkaConfig;
import org.kafnetty.mapper.ClientMapper;
import org.kafnetty.repository.ClientRepository;
import org.kafnetty.type.OPERATION_TYPE;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientServiceImpl implements ClientService {
    private static final Map<String, ClientDto> CHANNEL_USERS = new ConcurrentHashMap<>();
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final KafnettyKafkaConfig kafnettyKafkaConfig;

    private static boolean checkToken(ClientDto req) {
        return true;
    }

    @Override
    public ClientDto getChannelUser(String channelLongId) {
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
            ClientDto clientDto = CHANNEL_USERS.get(channelLongId);
            clientDto.setRoomId(roomId);
            CHANNEL_USERS.put(channelLongId, clientDto);
        }
    }

    @Override
    public void removeChannelUser(String channelLongId) {
        CHANNEL_USERS.remove(channelLongId);
    }

    @Override
    public ClientDto processMessage(ClientDto message, Channel channel) {
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
            CHANNEL_USERS.put(channel.id().asLongText(), clientMapper.ClientToChannelClientDto(client));
        }
        ClientDto result = clientMapper.ClientToChannelClientDto(client);
        result.setOperationType(message.getOperationType());
        return result;
    }

    @Override
    public void setClientAsSended(ClientDto channelClientDto) {
        Optional<Client> clientOptional = clientRepository.findById(channelClientDto.getId());
        if (clientOptional.isPresent()) {
            Client client = clientOptional.get();
            client.setSent(true);
            clientRepository.saveAndFlush(client);
        }
    }

    @Override
    public List<ClientDto> getNotSyncClients() {
        List<Client> clients = clientRepository.findAllByIsSentAndClusterId(false, kafnettyKafkaConfig.getGroupId());
        return clientMapper.ToClientDtoList(clients);
    }
}


