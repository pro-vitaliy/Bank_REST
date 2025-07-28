package com.example.bankcards.controller;

import com.example.bankcards.dto.user.UserCreateDTO;
import com.example.bankcards.dto.user.UserUpdateDTO;
import com.example.bankcards.entity.Role;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.utils.TestDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static com.example.bankcards.utils.TestDataFactory.TestUserData;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestDataFactory dataFactory;

    TestUserData testAdminData;
    TestUserData testUserData;

    @BeforeEach
    void beforeEach() {
        userRepository.deleteAll();
        testAdminData = dataFactory.createAdmin();
        testUserData = dataFactory.createUser();
    }

    @Test
    void shouldCreateWhenValidInput() throws Exception {
        var dto = new UserCreateDTO("newUser", "newPass123", Set.of(Role.ROLE_USER));
        var json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + testAdminData.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newUser"));
    }

    @Test
    void shouldReturnAllUsersList() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + testAdminData.token()))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Total-Count"));
    }

    @Test
    void shouldReturnUserById() throws Exception {
        mockMvc.perform(get("/api/users/" + testUserData.user().getId())
                        .header("Authorization", "Bearer " + testUserData.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(testUserData.user().getUsername()));
    }

    @Test
    void shouldUpdateWhenValid() throws Exception {
        var dto = new UserUpdateDTO(JsonNullable.of("updatedUser"), JsonNullable.of("updatedPass123"));
        var json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(patch("/api/users/" + testUserData.user().getId())
                        .header("Authorization", "Bearer " + testAdminData.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updatedUser"));
    }

    @Test
    void shouldDelete() throws Exception {
        mockMvc.perform(delete("/api/users/" + testUserData.user().getId())
                        .header("Authorization", "Bearer " + testAdminData.token()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/users/" + testAdminData.user().getId())
                        .header("Authorization", "Bearer " + testUserData.token()))
                .andExpect(status().isForbidden());
    }
}
