package com.example.bankcards.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super(String.format("Клиент с id '%s' не найден", id));
    }

    public UserNotFoundException(String username) {
        super(String.format("Клиент с логином '%s' не найден", username));
    }

    public UserNotFoundException(String username, String password) {
        super(String.format("Клиент с логином '%s' и паролем '%s' не найден", username, password));
    }
}
