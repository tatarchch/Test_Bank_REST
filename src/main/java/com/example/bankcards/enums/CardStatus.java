package com.example.bankcards.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CardStatus {

    ACTIVE("active"),
    BLOCKED("blocked"),
    EXPIRED("expired");

    private final String status;

}
