package com.example.bankcards.utils;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardUtils;
import com.example.bankcards.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.jasypt.util.text.AES256TextEncryptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class TestDataFactory {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final CardRepository cardRepository;
    private final AES256TextEncryptor cardNumberEncryptor;

    public record TestUserData(User user, String token) {}
    public record TestCardData(Card card, String plainCardNumber) {}

    public TestUserData createAdmin() {
        return createUser("admin", "admin123", Set.of(Role.ROLE_ADMIN));
    }

    public TestUserData createUser() {
        return createUser("user", "user123", Set.of(Role.ROLE_USER));
    }

    public TestUserData createUser(String username, String password) {
        return createUser(username, password, Set.of(Role.ROLE_USER));
    }

    public TestCardData createCardForUser(User owner, String plainCardNumber) {
        return createCardForUser(owner, plainCardNumber, CardStatus.ACTIVE);
    }

    public TestCardData createCardForUser(User owner, String plainCardNumber, CardStatus cardStatus) {
        Card card = new Card();
        card.setEncryptedCardNumber(cardNumberEncryptor.encrypt(plainCardNumber));
        card.setMaskedCardNumber(CardUtils.maskCardNumber(plainCardNumber));
        card.setCardHash(CardUtils.hash(plainCardNumber));
        card.setOwner(owner);
        card.setExpirationDate(LocalDate.now().plusYears(3));
        card.setBalance(new BigDecimal("500.00"));
        card.setCardStatus(cardStatus);
        cardRepository.save(card);

        return new TestCardData(card, plainCardNumber);
    }

    private TestUserData createUser(String username, String password, Set<Role> roles) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(roles);
        userRepository.save(user);

        List<String> rolesList = roles.stream()
                .map(Enum::name)
                .toList();

        String token = jwtUtils.generateToken(username, rolesList);
        return new TestUserData(user, token);
    }
}
