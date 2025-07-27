package com.example.bankcards.dto.card;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CardDTO {
    private Long id;
    private String maskedCardNumber;
    private Long ownerId;
    private LocalDate expirationDate;
    private String cardStatus;
    private BigDecimal balance;
    private LocalDate createdAt;
}
