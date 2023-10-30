package org.kafnetty.mapper;

import org.kafnetty.dto.channel.ChannelClientDto;
import org.kafnetty.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ClientMapper {
    ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);

    @Mapping(ignore = true, target = "messageType")
    @Mapping(ignore = true, target = "operationType")
    @Mapping(ignore = true, target = "ts")
    ChannelClientDto ClientToChannelClientDto(Client client);

    @Mapping(ignore = true, target = "id")
    Client ChannelClientDtoToClient(ChannelClientDto clientDto);
}

