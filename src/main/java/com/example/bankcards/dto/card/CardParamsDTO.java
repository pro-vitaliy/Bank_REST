package com.example.bankcards.dto.card;

import com.example.bankcards.entity.CardStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CardParamsDTO {
    private CardStatus status;
    private Long ownerId;
    private LocalDate expirationDateGte;
    private LocalDate expirationDateLte;
    private BigDecimal balanceGte;
    private BigDecimal balanceLte;
    private LocalDate createdAtGte;
    private LocalDate createdAtLte;
}
