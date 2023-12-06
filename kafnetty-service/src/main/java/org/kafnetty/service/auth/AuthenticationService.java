package org.kafnetty.service.auth;

import org.kafnetty.dto.TokenDto;
import org.kafnetty.dto.UserDto;

public interface AuthenticationService {
    TokenDto register(UserDto userDto);

    TokenDto authenticate(UserDto userDto);
}
