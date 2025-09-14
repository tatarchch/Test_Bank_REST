package com.example.bankcards.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CardStatus {

    ACTIVE("ACTIVE"),
    BLOCKED("BLOCKED"),
    EXPIRED("EXPIRED");

    public final String string;
}
