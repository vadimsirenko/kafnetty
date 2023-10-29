package ru.vasire.kafnetty.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import ru.vasire.kafnetty.dto.ClientDto;
import ru.vasire.kafnetty.dto.UserProfileDto;
import ru.vasire.kafnetty.entity.Client;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
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
