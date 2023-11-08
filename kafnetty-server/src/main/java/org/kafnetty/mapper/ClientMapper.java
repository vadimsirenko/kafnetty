package org.kafnetty.mapper;

import org.kafnetty.dto.ClientDto;
import org.kafnetty.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ClientMapper {
    ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);

    @Mapping(ignore = true, target = "messageType")
    @Mapping(ignore = true, target = "operationType")
    ClientDto ClientToChannelClientDto(Client client);

    @Mapping(ignore = true, target = "sent")
    Client ClientDtoToClient(ClientDto clientDto);

    List<ClientDto> ToClientDtoList(List<Client> clients);
}

