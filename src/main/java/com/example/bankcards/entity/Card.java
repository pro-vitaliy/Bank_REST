package com.example.bankcards.entity;

import com.example.bankcards.exception.CardOperationException;
import com.example.bankcards.exception.ErrorCode;
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
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

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

    @Version
    private Long version;

    @Column(nullable = false)
    private String encryptedCardNumber;

    @Column(nullable = false)
    private String maskedCardNumber;

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

    @CreatedDate
    private LocalDate createdAt;

    public boolean isExpired() {
        return CardStatus.EXPIRED == cardStatus
                || (expirationDate != null && expirationDate.isBefore(LocalDate.now()));
    }

    public void validateAvailableForOperation() {
        if (isExpired()) {
            throw new CardOperationException(ErrorCode.CARD_EXPIRED);
        }
        if (cardStatus == CardStatus.BLOCKED) {
            throw new CardOperationException(ErrorCode.CARD_BLOCKED);
        }
    }

    public void increaseBalance(BigDecimal amount) {
        balance = balance.add(amount);
    }

    public void decreaseBalance(BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new CardOperationException(ErrorCode.CARD_INSUFFICIENT_FUNDS);
        }
        balance = balance.subtract(amount);
    }
}
