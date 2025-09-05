package com.example.bankcards.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {

    ADMIN("ADMIN"),
    USER("USER");

    private final String string;
}
