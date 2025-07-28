package com.example.bankcards.controller;

import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.utils.TestDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.example.bankcards.utils.TestDataFactory.TestCardData;
import static com.example.bankcards.utils.TestDataFactory.TestUserData;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestDataFactory dataFactory;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private UserRepository userRepository;

    private TestUserData admin;
    private TestUserData owner;
    private TestUserData anotherUser;
    private TestCardData testCard;

    @BeforeEach
    void beforeEach() {
        userRepository.deleteAll();
        cardRepository.deleteAll();
        owner = dataFactory.createUser("owner", "owner123");
        anotherUser = dataFactory.createUser("stranger", "stranger123");
        admin = dataFactory.createAdmin();

        testCard = dataFactory.createCardForUser(owner.user(), "1234567812345678");
    }

    @Test
    void ownerCanGetCardById() throws Exception {
        mockMvc.perform(get("/api/cards/" + testCard.card().getId())
                        .header("Authorization", "Bearer " + owner.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testCard.card().getId()));
    }

    @Test
    void adminCanGetCardById() throws Exception {
        mockMvc.perform(get("/api/cards/" + testCard.card().getId())
                        .header("Authorization", "Bearer " + admin.token()))
                .andExpect(status().isOk());
    }

    @Test
    void strangerCannotGetCardById() throws Exception {
        mockMvc.perform(get("/api/cards/" + testCard.card().getId())
                        .header("Authorization", "Bearer " + anotherUser.token()))
                .andExpect(status().isForbidden());
    }

    @Test
    void ownerCanRevealFullNumber() throws Exception {
        mockMvc.perform(get("/api/cards/" + testCard.card().getId() + "/number")
                        .header("Authorization", "Bearer " + owner.token()))
                .andExpect(status().isOk())
                .andExpect(content().string(testCard.plainCardNumber()));
    }

    @Test
    void adminCanBlockCard() throws Exception {
        mockMvc.perform(patch("/api/cards/" + testCard.card().getId() + "/block")
                        .header("Authorization", "Bearer " + admin.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardStatus").value("BLOCKED"));
    }
}
