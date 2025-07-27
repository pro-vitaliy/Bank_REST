package com.example.bankcards.controller;

import com.example.bankcards.dto.card.CardCreateDTO;
import com.example.bankcards.dto.card.CardDTO;
import com.example.bankcards.dto.card.CardParamsDTO;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class CardController {
    private final CardService cardService;
    private final SecurityUtils securityUtils;

    @PreAuthorize("hasRole('ADMIN') or @securityUtils.isOwner(#id)")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/cards/{id}")
    public CardDTO getCardById(@PathVariable Long id) {
        return cardService.findById(id);
    }

    @PreAuthorize("hasRole('ADMIN') or @securityUtils.isOwner(#id)")
    @GetMapping("/cards/{id}/number")
    public ResponseEntity<String> revealFullNumber(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.getFullCardNumber(id));
    }

    @PreAuthorize("hasRole('ADMIN') or @securityUtils.isOwner(#id)")
    @GetMapping("/cards/{id}/balance")
    public ResponseEntity<BigDecimal> getCardBalance(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.getBalance(id));
    }

    @PreAuthorize("@securityUtils.isOwner(#userId)")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/users/{userId}/cards")
    public Page<CardDTO> getCardsByUserId(@PathVariable Long userId, CardParamsDTO params, Pageable pageable) {
        return cardService.getUserCards(userId, params, pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/cards")
    public Page<CardDTO> getAllCards(CardParamsDTO params, Pageable pageable) {
        return cardService.getAll(params, pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/cards/{id}/activate")
    public CardDTO activateCard(@PathVariable Long id) {
        return cardService.activate(id);
    }

    @PreAuthorize("hasRole('ADMIN') or @securityUtils.isOwner(#id)")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/cards/{id}/block")
    public CardDTO blockCard(@PathVariable Long id) {
        return cardService.block(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/cards")
    public CardDTO create(@Valid @RequestBody CardCreateDTO cardData) {
        return cardService.create(cardData);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/cards/{id}")
    public void delete(@PathVariable Long id) {
        cardService.delete(id);
    }
}
