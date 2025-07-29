package com.example.bankcards.startup;

import com.example.bankcards.dto.user.UserCreateDTO;
import com.example.bankcards.entity.Role;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@RequiredArgsConstructor
@Component
public class AdminInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final UserService userService;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.existsByUsername(adminUsername)) {
            return;
        }
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setUsername(adminUsername);
        userCreateDTO.setPassword(adminPassword);
        userCreateDTO.setRoles(Set.of(Role.ROLE_ADMIN));

        userService.create(userCreateDTO);
    }
}
