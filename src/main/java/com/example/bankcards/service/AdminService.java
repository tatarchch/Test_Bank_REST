package com.example.bankcards.service;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.enums.UserRole;
import com.example.bankcards.exception.OtherException;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder encoder;

    public List<UserDto> getAllAdmins() {
        return repository.findAllByRole(UserRole.ADMIN.getString()).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional
    public UserDto addNewAdmin(UserDto userDto) {
        return Optional.of(userDto)
                .map(dto -> {
                    dto.setRole(UserRole.ADMIN.getString());
                    return dto;
                })
                .map(mapper::toEntity)
                .map(client -> {
                    client.setPassword(encoder.encode(client.getPassword()));
                    return client;
                })
                .map(repository::save)
                .map(mapper::toDto)
                .orElseThrow(OtherException::new);
    }

}
