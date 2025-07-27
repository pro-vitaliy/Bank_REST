package com.example.bankcards.util;

import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jasypt.util.digest.Digester;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CardUtils {
    private static final int VISIBLE_DIGITS = 4;
    private static final Digester DIGESTER = new Digester();

    static {
        DIGESTER.setAlgorithm("SHA-256");
    }

    public static String hash(String cardNum) {
        byte[] hash = DIGESTER.digest(cardNum.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hash);
    }

    public static String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            throw new IllegalArgumentException("Количество цифр должно быть не менее " + VISIBLE_DIGITS);
        }
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - VISIBLE_DIGITS);
    }
}
