package org.kafnetty.service;

import org.kafnetty.dto.TokenDto;
import org.kafnetty.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationService {
    TokenDto register(UserDto userDto);

    TokenDto authenticate(UserDto userDto);
}
