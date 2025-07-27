package com.example.bankcards.dto.transfer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransferRequest {
    @NotBlank
    private String cardNumFrom;

    @NotBlank
    private String carsNumTo;

    @NotNull
    @Positive(message = "Сумма перевода должна быть больше 0")
    private BigDecimal amount;
}
