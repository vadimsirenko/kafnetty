package org.kafnetty.service;

import lombok.RequiredArgsConstructor;
import org.kafnetty.mapper.UserMapper;
import org.kafnetty.store.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return UserMapper.INSTANCE.UserToUserDto(
                userRepository.findByEmail(username)
                .orElseThrow());
    }
}
