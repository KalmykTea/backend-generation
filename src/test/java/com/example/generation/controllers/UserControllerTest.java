package com.example.generation.controllers;

import com.example.generation.dtos.RequestDTOs.AccountLimitsRequestDTO;
import com.example.generation.entities.User;
import com.example.generation.enums.AccountType;
import com.example.generation.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private User pending;
    List<AccountLimitsRequestDTO> validLimitDTOs;
    List<AccountLimitsRequestDTO> invalidLimitDTOs;

    @BeforeEach
    void setUp() {
        pending = userRepository.findByEmail("pending@test.com")
                .orElseThrow(() -> new IllegalStateException("Test fixture missing user pending@test.com"));

        validLimitDTOs = List.of(
                new AccountLimitsRequestDTO(
                        null,
                        AccountType.CHECKING,
                        BigDecimal.valueOf(-100),
                        BigDecimal.valueOf(100)
                ),
                new AccountLimitsRequestDTO(
                        null,
                        AccountType.SAVINGS,
                        BigDecimal.valueOf(-500),
                        BigDecimal.valueOf(100)
                )
        );
        invalidLimitDTOs = List.of(
                new AccountLimitsRequestDTO(
                        null,
                        AccountType.CHECKING,
                        BigDecimal.valueOf(100),
                        BigDecimal.valueOf(-100)
                ),
                new AccountLimitsRequestDTO(
                        null,
                        AccountType.CHECKING,
                        BigDecimal.valueOf(500),
                        BigDecimal.valueOf(-100)
                )
        );
    }

    // approve users tests
    @Test
    @WithUserDetails("employee@test.com")
    void approveUser_returnsBadRequestForInvalidPayload() throws Exception {
        mockMvc.perform(post("/users/" + pending.getId() + "/approve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidLimitDTOs)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Duplicate account types are not allowed"));
    }

    @Test
    @WithUserDetails("employee@test.com")
    void approveUser_returnsIsOk() throws Exception {
        mockMvc.perform(post("/users/" + pending.getId() + "/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLimitDTOs)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(pending.getId()))
                .andExpect(jsonPath("$.firstName").value(pending.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(pending.getLastName()));
    }

    @Test
    void approveUser_returnsForbiddenWhenUnauthorized() throws Exception {
        mockMvc.perform(post("/users/" + pending.getId() + "/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLimitDTOs)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("employee@test.com")
    void approveUser_nonExistentUser_returns404() throws Exception {
        mockMvc.perform(post("/users/" + 9999L + "/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLimitDTOs)))
                .andExpect(status().isNotFound());
    }

    // get pending users tests
    @Test
    @WithUserDetails(value = "employee@test.com")
    void getPendingUsers_employeeToken_returns200() throws Exception {
        mockMvc.perform(get("/users/pending"))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(value = "customer@test.com")
    void getPendingUsers_customerToken_returns403() throws Exception {
        mockMvc.perform(get("/users/pending"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getPendingUsers_noToken_returns403() throws Exception {
        mockMvc.perform(get("/users/pending"))
                .andExpect(status().isForbidden());
    }
}
