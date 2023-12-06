package org.kafnetty.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.kafnetty.dto.UserDto;
import org.kafnetty.entity.User;
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
    @Mapping(ignore = true, target = "password")
    @Mapping(ignore = true, target = "ts")
    @Mapping(ignore = true, target = "clusterId")
    UserDto UserToUserDto(User user);

    @Mapping(ignore = true, target = "sent")
    @Mapping(ignore = true, target = "role")
    @Mapping(ignore = true, target = "authorities")
    User UserDtoToUser(UserDto userDto);

    List<UserDto> ToUserDtoList(List<User> users);

}

