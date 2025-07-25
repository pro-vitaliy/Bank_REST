package com.example.bankcards.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    USER_NOT_FOUND("Пользователь не найден"),
    USER_ALREADY_EXISTS("Пользователь с таким именем уже существует"),
    CARD_NOT_FOUND("Карта не найдена"),
    CARD_EXPIRED("Операция невозможна: карта просрочена"),
    CARD_BLOCKED("Операция невозможна: карта заблокирована"),
    CARD_ALREADY_BLOCKED("Карта уже заблокирована"),
    CARD_ALREADY_ACTIVE("Карта уже активирована"),
    CARD_ALREADY_EXISTS("Такая карта уже существует");

    private final String defaultMessage;

    ErrorCode(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }
}
