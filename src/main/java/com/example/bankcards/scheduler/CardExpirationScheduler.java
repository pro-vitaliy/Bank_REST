package com.example.bankcards.scheduler;

import com.example.bankcards.service.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CardExpirationScheduler {
    private final CardService cardService;

    @Scheduled(cron = "0 0 4 * * *")
    public void updateExpiredCardStatuses() {
        log.info("Starting scheduled check for expired cards...");
        int updatedCount = cardService.markCardsAsExpired();
        log.info("Expired card check completed. Status updated to EXPIRED for {} cards", updatedCount);
    }
}
