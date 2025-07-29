package com.example.bankcards.service;

import com.example.bankcards.dto.card.CardCreateDTO;
import com.example.bankcards.dto.card.CardDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.CardOperationException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.specification.CardSpecification;
import org.jasypt.util.text.AES256TextEncryptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardMapper mapper;

    @Mock
    private AES256TextEncryptor encryptor;

    @Mock
    private CardSpecification specBuilder;

    @InjectMocks
    private CardService cardService;

    @Test
    void findShouldReturnsCardDto() {
        Long id = 1L;
        Card card = new Card();
        CardDTO dto = new CardDTO();

        when(cardRepository.findById(id)).thenReturn(Optional.of(card));
        when(mapper.map(card)).thenReturn(dto);
        CardDTO result = cardService.findById(id);

        assertEquals(dto, result);
    }

    @Test
    void findShouldThrowsException() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cardService.findById(1L));
    }

    @Test
    void getFullCardNumberShouldDecryptsValue() {
        Long id = 1L;
        Card card = new Card();
        card.setEncryptedCardNumber("enc123");

        when(cardRepository.findById(id)).thenReturn(Optional.of(card));
        when(encryptor.decrypt("enc123")).thenReturn("4111222233334444");

        String result = cardService.getFullCardNumber(id);

        assertEquals("4111222233334444", result);
    }

    @Test
    void getBalanceShouldReturnsBalance() {
        Long id = 1L;
        Card card = new Card();
        card.setBalance(new BigDecimal("100.00"));

        when(cardRepository.findById(id)).thenReturn(Optional.of(card));

        BigDecimal result = cardService.getBalance(id);

        assertEquals(new BigDecimal("100.00"), result);
    }

    @Test
    void createShouldReturnsCardDto() {
        CardCreateDTO dto = new CardCreateDTO();
        Card card = new Card();
        CardDTO expected = new CardDTO();

        when(mapper.map(dto)).thenReturn(card);
        when(cardRepository.save(card)).thenReturn(card);
        when(mapper.map(card)).thenReturn(expected);

        CardDTO result = cardService.create(dto);

        assertEquals(expected, result);
    }

    @Test
    void createShouldThrowsException() {
        CardCreateDTO dto = new CardCreateDTO();
        Card card = new Card();

        when(mapper.map(dto)).thenReturn(card);
        when(cardRepository.save(card)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(CardOperationException.class, () -> cardService.create(dto));
    }

    @Test
    void activateShouldThrowsExpired() {
        Card card = Mockito.spy(new Card());
        card.setCardStatus(CardStatus.NEW);
        when(card.isExpired()).thenReturn(true);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        assertThrows(CardOperationException.class, () -> cardService.activate(1L));
    }

    @Test
    void activateShouldSetsActive() {
        Card card = new Card();
        card.setCardStatus(CardStatus.NEW);
        card.setExpirationDate(LocalDate.now().plusDays(1));

        CardDTO dto = new CardDTO();

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);
        when(mapper.map(card)).thenReturn(dto);

        CardDTO result = cardService.activate(1L);

        assertEquals(CardStatus.ACTIVE, card.getCardStatus());
        assertEquals(dto, result);
    }

    @Test
    void blockShouldSetsBlocked() {
        Card card = new Card();
        card.setCardStatus(CardStatus.ACTIVE);
        card.setExpirationDate(LocalDate.now().plusDays(5));

        CardDTO dto = new CardDTO();

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);
        when(mapper.map(card)).thenReturn(dto);

        CardDTO result = cardService.block(1L);

        assertEquals(CardStatus.BLOCKED, card.getCardStatus());
        assertEquals(dto, result);
    }

    @Test
    void deleteShouldDeletes() {
        when(cardRepository.existsById(1L)).thenReturn(true);
        cardService.delete(1L);

        verify(cardRepository).deleteById(1L);
    }

    @Test
    void deleteShouldThrowsException() {
        when(cardRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> cardService.delete(1L));
    }

    @Test
    void markCardsAsExpiredShouldUpdatesStatus() {
        Card card1 = new Card();
        card1.setCardStatus(CardStatus.ACTIVE);
        Card card2 = new Card();
        card2.setCardStatus(CardStatus.ACTIVE);

        List<Card> expired = List.of(card1, card2);

        when(cardRepository.findAllByExpirationDateBeforeAndCardStatus(any(), eq(CardStatus.ACTIVE)))
                .thenReturn(expired);

        int result = cardService.markCardsAsExpired();

        assertEquals(2, result);
        assertEquals(CardStatus.EXPIRED, card1.getCardStatus());
        assertEquals(CardStatus.EXPIRED, card2.getCardStatus());
        verify(cardRepository).saveAll(expired);
    }
}

