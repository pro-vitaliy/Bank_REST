package com.example.bankcards.service;

import com.example.bankcards.dto.card.CardCreateDTO;
import com.example.bankcards.dto.card.CardDTO;
import com.example.bankcards.dto.card.CardParamsDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.CardOperationException;
import com.example.bankcards.exception.ErrorCode;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.specification.CardSpecification;
import lombok.RequiredArgsConstructor;
import org.jasypt.util.text.AES256TextEncryptor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CardService {
    private final CardRepository cardRepository;
    private final CardMapper mapper;
    private AES256TextEncryptor encryptor;
    private CardSpecification specBuilder;

    public CardDTO findById(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CARD_NOT_FOUND));
        return mapper.map(card);
    }

    public Page<CardDTO> getAll(CardParamsDTO params, Pageable pageable) {
        Specification<Card> spec = specBuilder.build(params);
        return cardRepository.findAll(spec, pageable)
                .map(mapper::map);
    }

    public Page<CardDTO> getUserCards(Long userId, CardParamsDTO params, Pageable pageable) {
        params.setOwnerId(userId);
        Specification<Card> spec = specBuilder.build(params);
        return cardRepository.findAll(spec, pageable)
                .map(mapper::map);
    }

    public String getFullCardNumber(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CARD_NOT_FOUND));
        return encryptor.decrypt(card.getEncryptedCardNumber());
    }

    public BigDecimal getBalance(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CARD_NOT_FOUND));
        return card.getBalance();
    }

    public CardDTO create(CardCreateDTO cardData) {
        Card card = mapper.map(cardData);
        try {
            cardRepository.save(card);
            return mapper.map(card);
        } catch (DataIntegrityViolationException e) {
            throw new CardOperationException(ErrorCode.CARD_ALREADY_EXISTS);
        }
    }

    public CardDTO activate(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CARD_NOT_FOUND));

        if (CardStatus.ACTIVE == card.getCardStatus()) {
            throw new CardOperationException(ErrorCode.CARD_ALREADY_ACTIVE);
        }

        if (card.isExpired()) {
            throw new CardOperationException(ErrorCode.CARD_EXPIRED);
        }

        card.setCardStatus(CardStatus.ACTIVE);
        cardRepository.save(card);
        return mapper.map(card);
    }

    public CardDTO block(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CARD_NOT_FOUND));

        if (card.isExpired()) {
            throw new CardOperationException(ErrorCode.CARD_EXPIRED);
        }

        if (card.getCardStatus() == CardStatus.BLOCKED) {
            throw new CardOperationException(ErrorCode.CARD_ALREADY_BLOCKED);
        }

        card.setCardStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
        return mapper.map(card);
    }

    public void delete(Long cardId) {
        if (!cardRepository.existsById(cardId)) {
            throw new ResourceNotFoundException(ErrorCode.CARD_NOT_FOUND);
        }
        cardRepository.deleteById(cardId);
    }

    public int markCardsAsExpired() {
        List<Card> expiredCards = cardRepository.findAllByExpirationDateBeforeAndCardStatus(
                LocalDate.now(), CardStatus.ACTIVE
        );
        expiredCards.forEach(card -> card.setCardStatus(CardStatus.EXPIRED));
        cardRepository.saveAll(expiredCards);
        return expiredCards.size();
    }


}
