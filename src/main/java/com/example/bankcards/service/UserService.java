package com.example.bankcards.service;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.enums.UserRole;
import com.example.bankcards.exception.UserAlreadyExistsException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder encoder;

    public List<UserDto> getAllUsers() {
        return repository.findAllByRole(UserRole.USER.getString()).stream()
                .map(mapper::toDto)
                .toList();
    }

    public UserDto getUserById(Long id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public UserDto getUserByUsernameAndPassword(String username, String password) {
        return repository.findUserByUsernameAndRole(username, UserRole.USER.getString())
                .filter(user -> encoder.matches(password, user.getPassword()))
                .map(mapper::toDto)
                .orElseThrow(() -> new UserNotFoundException(username, password));
    }

    public UserDto addNewUser(UserDto userDto) {
        return Optional.of(userDto)
                .map(UserDto::getUsername)
                .map(repository::existsUserByUsername)
                .filter(Boolean.FALSE::equals)
                .map(dto -> {
                    userDto.setRole(UserRole.USER.getString());
                    return userDto;
                })
                .map(mapper::toEntity)
                .map(user -> {
                    user.setPassword(encoder.encode(user.getPassword()));
                    return user;
                })
                .map(repository::save)
                .map(mapper::toDto)
                .orElseThrow(() -> new UserAlreadyExistsException(userDto));
    }

    public void deleteUser(UserDto userDto) {
        repository.delete(mapper.toEntity(userDto));
    }

    public void deleteUserById(Long id) {
        repository.deleteById(id);
    }

}
