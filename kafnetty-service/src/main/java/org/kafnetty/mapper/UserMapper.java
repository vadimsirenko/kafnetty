package org.kafnetty.mapper;

import org.kafnetty.dto.UserDto;
import org.kafnetty.store.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(ignore = true, target = "messageType")
    @Mapping(ignore = true, target = "operationType")
    @Mapping(ignore = true, target = "authorities")
    @Mapping(ignore = true, target = "ts")
    @Mapping(ignore = true, target = "clusterId")
    UserDto UserToUserDto(User user);

    @Mapping(ignore = true, target = "sent")
    User UserDtoToUser(UserDto userDto);

    List<UserDto> ToUserDtoList(List<User> users);

}

