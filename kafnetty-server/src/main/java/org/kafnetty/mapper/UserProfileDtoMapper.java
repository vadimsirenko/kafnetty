package org.kafnetty.mapper;

import org.kafnetty.dto.UserProfileDto;
import org.kafnetty.dto.channel.ChannelClientDto;
import org.kafnetty.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserProfileDtoMapper {
    UserProfileDtoMapper INSTANCE = Mappers.getMapper(UserProfileDtoMapper.class);

    UserProfileDto ClientToUserProfileDto(Client client);

    @Mapping(ignore = true, target = "token")
    @Mapping(ignore = true, target = "messageType")
    @Mapping(ignore = true, target = "operationType")
    @Mapping(ignore = true, target = "clusterId")
    @Mapping(ignore = true, target = "ts")
    ChannelClientDto UserProfileDtoToChannelClientDto(UserProfileDto userProfileDto);
}
