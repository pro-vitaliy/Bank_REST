package com.example.bankcards.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Table(name = "cards")
@Entity
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String encryptedCardNumber;

    @Column(nullable = false)
    private String maskedCardNumber;

//    TODO: подумать над индексами мб на это поле и параметр length = ?
    @Column(nullable = false, unique = true)
    private String cardHash;

    @NotNull
    @ManyToOne
    @JoinColumn(nullable = false)
    private User owner;

    @NotNull
    @Column(nullable = false)
    private LocalDate expirationDate;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private CardStatus cardStatus = CardStatus.ACTIVE;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;
}
