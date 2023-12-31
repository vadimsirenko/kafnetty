package org.kafnetty.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kafnetty.dto.TokenDto;
import org.kafnetty.dto.UserDto;
import org.kafnetty.store.entity.User;
import org.kafnetty.exception.InvalidPasswordException;
import org.kafnetty.mapper.UserMapper;
import org.kafnetty.store.repository.UserRepository;
import org.kafnetty.type.Role;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService{
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(16);
    private final UserRepository userRepository;
    @Override
    public TokenDto register(UserDto userDto) {
        User user = UserMapper.INSTANCE.UserDtoToUser(userDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        user = userRepository.saveAndFlush(user);
        return new TokenDto(jwtService.generateToken(
                UserMapper.INSTANCE.UserToUserDto(user)));
    }

    @Override
    public TokenDto authenticate(UserDto userDto) {
        User user = userRepository.findByEmail(userDto.getEmail()).orElseThrow();
        if(!passwordEncoder.matches(userDto.getPassword(), user.getPassword())){
            throw new InvalidPasswordException();
        }
        return new TokenDto(jwtService.generateToken(UserMapper.INSTANCE.UserToUserDto(user)));
    }
}
