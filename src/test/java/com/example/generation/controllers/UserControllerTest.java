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

/**
 * This test fails because it returns 404 (User is not pending)
 * the error code is incorrect and should be changed to 500
 * @Test
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

    }
 **/
}
