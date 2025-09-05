package com.example.bankcards.service;

import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;

    public UsernamePasswordAuthenticationToken authentication(String username, String password) {
        return repository.findUserByUsername(username)
                .filter(user -> encoder.matches(password, user.getPassword()))
                .map(user -> User.builder().username(user.getUsername()).password(user.getPassword()).roles(user.getRole()).build())
                .map(user -> new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        null,
                        user.getAuthorities()
                ))
                .orElseThrow(() -> new UsernameNotFoundException("Неверный логин '" + username + "' или пароль к нему"));
    }

}
