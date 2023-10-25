package ru.vasire.kafnetty.server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.vasire.kafnetty.server.dto.ClientDto;
import ru.vasire.kafnetty.server.entity.Client;
import ru.vasire.kafnetty.server.dto.UserProfileDto;

@Mapper
public interface UserProfileDtoMapper {
    UserProfileDtoMapper INSTANCE = Mappers.getMapper(UserProfileDtoMapper.class);
    UserProfileDto ClientToUserProfileDto(Client client);
    @Mapping(ignore = true, target = "email")
    @Mapping(ignore = true, target = "token")
    @Mapping(ignore = true, target = "messageType")
    @Mapping(ignore = true, target = "operationType")
    @Mapping(ignore = true, target = "ts")
    ClientDto UserProfileDtoToClientDto(UserProfileDto userProfileDto);
}
