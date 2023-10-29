package ru.vasire.kafnetty.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import ru.vasire.kafnetty.dto.ClientDto;
import ru.vasire.kafnetty.entity.Client;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ClientMapper {
    ClientMapper INSTANCE = Mappers.getMapper( ClientMapper.class );
    @Mapping(ignore = true, target = "messageType")
    @Mapping(ignore = true, target = "operationType")
    @Mapping(ignore = true, target = "ts")
    ClientDto ClientToClientDto(Client client);

    @Mapping(ignore = true, target = "id")
    Client ClientDtoToClient(ClientDto clientDto);
}

