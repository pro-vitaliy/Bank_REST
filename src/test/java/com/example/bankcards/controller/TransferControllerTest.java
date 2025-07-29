package com.example.bankcards.controller;

import com.example.bankcards.dto.transfer.TransferRequest;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.utils.TestDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardRepository cardRepository;

    @BeforeEach
    void beforeEach() {
        userRepository.deleteAll();
        cardRepository.deleteAll();
    }

    @Test
    void transferShouldReturnsOk() throws Exception {
        var userData = testDataFactory.createUser("demo", "demo123");
        var user = userData.user();
        var token = userData.token();

        var cardFrom = testDataFactory.createCardForUser(user, "1111222233334444");
        var cardTo = testDataFactory.createCardForUser(user, "5555666677778888");

        cardRepository.saveAll(List.of(cardFrom.card(), cardTo.card()));

        var request = new TransferRequest();
        request.setCardNumFrom(cardFrom.plainCardNumber());
        request.setCarsNumTo(cardTo.plainCardNumber());
        request.setAmount(new BigDecimal("200.00"));

        mockMvc.perform(post("/api/transfer")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}

