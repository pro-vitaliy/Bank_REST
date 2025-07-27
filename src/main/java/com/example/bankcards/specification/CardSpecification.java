package com.example.bankcards.specification;

import com.example.bankcards.dto.card.CardParamsDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class CardSpecification {
    public Specification<Card> build(CardParamsDTO params) {
        return statusEquals(params.getStatus())
                .and(ownerIdEquals(params.getOwnerId()))
                .and(expirationDateGte(params.getExpirationDateGte()))
                .and(expirationDateLte(params.getExpirationDateLte()))
                .and(balanceGte(params.getBalanceGte()))
                .and(balanceLte(params.getBalanceLte()))
                .and(createdAtGte(params.getCreatedAtGte()))
                .and(createdAtLte(params.getCreatedAtLte()));
    }

    private Specification<Card> statusEquals(CardStatus status) {
        return (root, query, cb) -> {
            return status == null ? cb.conjunction() : cb.equal(root.get("cardStatus"), status);
        };
    }

    private Specification<Card> ownerIdEquals(Long ownerId) {
        return (root, query, cb) -> {
            return ownerId == null ? cb.conjunction() : cb.equal(root.get("owner").get("id"), ownerId);
        };
    }

    private Specification<Card> expirationDateGte(LocalDate date) {
        return (root, query, cb) -> {
           return date == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("expirationDate"), date);
        };
    }

    private Specification<Card> expirationDateLte(LocalDate date) {
        return (root, query, cb) -> {
            return date == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("expirationDate"), date);
        };
    }

    private Specification<Card> balanceGte(BigDecimal balance) {
        return (root, query, cb) -> {
            return balance == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("balance"), balance);
        };
    }

    private Specification<Card> balanceLte(BigDecimal balance) {
        return (root, query, cb) -> {
            return balance == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("balance"), balance);
        };
    }

    private Specification<Card> createdAtGte(LocalDate date) {
        return (root, query, cb) -> {
            return date == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("createdAt"), date);
        };
    }

    private Specification<Card> createdAtLte(LocalDate date) {
        return (root, query, cb) -> {
            return date == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("createdAt"), date);
        };
    }
}
