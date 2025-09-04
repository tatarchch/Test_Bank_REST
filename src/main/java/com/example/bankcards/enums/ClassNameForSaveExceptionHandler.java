package com.example.bankcards.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ClassNameForSaveExceptionHandler {

    ADMIN_CONTROLLER("AdminController"),
    CARD_CONTROLLER("CardController"),
    USER_CONTROLLER("UserController");

    public static ClassNameForSaveExceptionHandler getByValue(String value) {
        return Arrays.stream(ClassNameForSaveExceptionHandler.values())
                .filter(nameEnum -> nameEnum.getClassName().equals(value))
                .findFirst()
                .orElse(null);
    }

    private final String className;

}
