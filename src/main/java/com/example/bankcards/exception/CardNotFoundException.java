package com.example.bankcards.exception;

import com.example.bankcards.dto.CardDto;

public class CardNotFoundException extends RuntimeException {

    public CardNotFoundException(Long id) {
        super(String.format("Карта с айди '%s' не найдена", id));
    }

    public CardNotFoundException(String cardNumber) {
        super(String.format("Карта с номером '%s' не найдена", cardNumber));
    }

    public CardNotFoundException() {
        super("Ошибка данных карты");
    }
}
