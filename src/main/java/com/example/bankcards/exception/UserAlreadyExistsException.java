package com.example.bankcards.exception;

import com.example.bankcards.dto.UserDto;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(UserDto userDto) {
        super(String.format("Пользователь с логином '%s' уже существует", userDto.getUsername()));
    }

}
