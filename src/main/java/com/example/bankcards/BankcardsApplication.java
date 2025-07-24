package com.example.bankcards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BankcardsApplication {
    public static void main(String[] args) {
        SpringApplication.run(BankcardsApplication.class, args);
    }
}
