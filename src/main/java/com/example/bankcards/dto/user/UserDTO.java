package com.example.bankcards.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class UserDTO {
    private Long id;
    private String username;
    private List<String> maskedCardNumbers;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
