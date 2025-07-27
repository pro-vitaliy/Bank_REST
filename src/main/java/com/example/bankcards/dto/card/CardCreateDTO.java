package com.example.bankcards.dto.card;

import com.example.bankcards.entity.CardStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CardCreateDTO {

    @NotNull
    @Pattern(regexp = "\\d{16}", message = "Номер карты должен содержать 16 цифр")
    private String cardNumber;

    @NotNull
    private Long ownerId;

    @NotNull
    @Future
    private LocalDate expirationDate;

    private CardStatus cardStatus;

    @Digits(integer = 17, fraction = 2)
    @DecimalMin(value = "0.00", message = "Баланс не должен быть отрицательным")
    private BigDecimal balance;
}
