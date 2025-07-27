package com.example.bankcards.service;

import com.example.bankcards.dto.AuthRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ErrorCode;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public String authenticateAndGenerateToken(AuthRequest authRequest) {
        var auth = new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());
        authenticationManager.authenticate(auth);

        User user = userRepository.findByUsername(authRequest.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));

        List<String> roles = user.getRoles()
                .stream()
                .map(Enum::name)
                .toList();

        return jwtUtils.generateToken(user.getUsername(), roles);
    }
}
