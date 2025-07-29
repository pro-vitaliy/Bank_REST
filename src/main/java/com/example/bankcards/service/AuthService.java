package com.example.bankcards.service;

import com.example.bankcards.dto.AuthRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ErrorCode;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public String authenticateAndGenerateToken(AuthRequest authRequest) {
        String username = authRequest.getUsername();
        log.info("Попытка входа: '{}'", username);

        try {
            var auth = new UsernamePasswordAuthenticationToken(username, authRequest.getPassword());
            authenticationManager.authenticate(auth);
        } catch (Exception ex) {
            log.warn("Ошибка аутентификации пользователя '{}': {}", username, ex.getClass().getSimpleName());
            throw ex;
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Пользователь '{}' не найден в базе", username);
                    return new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND);
                });

        List<String> roles = user.getRoles()
                .stream()
                .map(Enum::name)
                .toList();

        log.info("Успешная аутентификация пользователя '{}', роли: {}", username, roles);
        return jwtUtils.generateToken(user.getUsername(), roles);
    }
}
