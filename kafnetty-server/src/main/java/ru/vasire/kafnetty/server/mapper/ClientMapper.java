package ru.vasire.kafnetty.server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.vasire.kafnetty.server.entity.Client;
import ru.vasire.kafnetty.server.dto.ClientDto;

@Mapper
public interface ClientMapper {
    ClientMapper INSTANCE = Mappers.getMapper( ClientMapper.class );
    @Mapping(ignore = true, target = "messageType")
    @Mapping(ignore = true, target = "operationType")
    @Mapping(ignore = true, target = "ts")
    ClientDto ClientToClientDto(Client client);

    @Mapping(ignore = true, target = "id")
    Client ClientDtoToClient(ClientDto clientDto);
}

