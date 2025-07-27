package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.exception.CardOperationException;
import com.example.bankcards.exception.ErrorCode;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.CardUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class TransferService {
    private final CardRepository cardRepository;

    @Retryable(
            retryFor = OptimisticLockingFailureException.class,
            maxAttempts = 2,
            backoff = @Backoff(delay = 100)
    )
    @Transactional
    public void transferMoney(String cardNumFrom, String cardNumTo, BigDecimal amount) {
        if (cardNumFrom.equals(cardNumTo)) {
            throw new CardOperationException(ErrorCode.SAME_CARD_TRANSFER);
        }
        String hashFrom = CardUtils.hash(cardNumFrom);
        String hashTo = CardUtils.hash(cardNumTo);

        Card cardFrom = cardRepository.findByCardHash(hashFrom)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CARD_NOT_FOUND));

        Card cardTo = cardRepository.findByCardHash(hashTo)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CARD_NOT_FOUND));

        cardFrom.validateAvailableForOperation();
        cardTo.validateAvailableForOperation();
        cardFrom.decreaseBalance(amount);
        cardTo.increaseBalance(amount);

        cardRepository.save(cardFrom);
        cardRepository.save(cardTo);
    }

    @Recover
    public void recover(String cardNumFrom, String cardNumTo, BigDecimal amount) {
        throw new CardOperationException(ErrorCode.TRANSFER_FAILED);
    }
}
