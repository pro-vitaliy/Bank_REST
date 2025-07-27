package com.example.bankcards.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "Пользователь не найден"),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "Пользователь с таким именем уже существует"),
    CARD_NOT_FOUND(HttpStatus.NOT_FOUND, "Карта не найдена"),
    CARD_EXPIRED(HttpStatus.FORBIDDEN, "Операция невозможна: карта просрочена"),
    CARD_BLOCKED(HttpStatus.FORBIDDEN, "Операция невозможна: карта заблокирована"),
    CARD_ALREADY_BLOCKED(HttpStatus.CONFLICT, "Карта уже заблокирована"),
    CARD_ALREADY_ACTIVE(HttpStatus.CONFLICT, "Карта уже активирована"),
    CARD_ALREADY_EXISTS(HttpStatus.CONFLICT, "Такая карта уже существует"),
    CARD_INSUFFICIENT_FUNDS(HttpStatus.BAD_REQUEST, "Недостаточно средств на карте"),
    SAME_CARD_TRANSFER(HttpStatus.CONFLICT, "Перевод на одну и ту же карту невозможен"),
    TRANSFER_FAILED(HttpStatus.CONFLICT, "Ошибка при выполнении перевода. Повторите попытку позже");

    private final HttpStatus httpStatus;
    private final String defaultMessage;

    ErrorCode(HttpStatus httpStatus, String defaultMessage) {
        this.httpStatus = httpStatus;
        this.defaultMessage = defaultMessage;
    }
}
