package org.kafnetty.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kafnetty.dto.TokenDto;
import org.kafnetty.dto.UserDto;
import org.kafnetty.entity.User;
import org.kafnetty.exceptions.InvalidPasswordException;
import org.kafnetty.mapper.UserMapper;
import org.kafnetty.repository.UserRepository;
import org.kafnetty.type.Role;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService{
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserRepository userRepository;
    @Override
    public TokenDto register(UserDto userDto) {
        User user = UserMapper.INSTANCE.UserDtoToUser(userDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        user = userRepository.saveAndFlush(user);
        return new TokenDto(jwtService.generateToken(user));
    }

    @Override
    public TokenDto authenticate(UserDto userDto) {
        final String  password = passwordEncoder.encode(userDto.getPassword());
        User user = userRepository.findByEmail(userDto.getEmail()).orElseThrow();
        if(!user.getPassword().equals(password)){
            throw new InvalidPasswordException();
        }
        return new TokenDto(jwtService.generateToken(user));
    }
}
