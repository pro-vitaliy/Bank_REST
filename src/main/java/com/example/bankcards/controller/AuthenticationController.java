package com.example.bankcards.controller;

import com.example.bankcards.dto.AuthRequest;
import com.example.bankcards.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class AuthenticationController {
    private final AuthService authService;

    @PostMapping("/login")
    public String create(@RequestBody AuthRequest authRequest) {
        return authService.authenticateAndGenerateToken(authRequest);
    }
}
