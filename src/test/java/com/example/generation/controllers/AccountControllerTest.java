package com.example.generation.controllers;

import com.example.generation.dtos.RequestDTOs.AccountLimitsRequestDTO;
import com.example.generation.entities.Account;
import com.example.generation.enums.AccountType;
import com.example.generation.repositories.AccountRepository;
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

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ObjectMapper objectMapper;

    Account existingCheckingAccount;
    AccountLimitsRequestDTO validLimitsRequestDTO;
    AccountLimitsRequestDTO invalidLimitsRequestDTO;

    @BeforeEach
    void setup()
    {
        existingCheckingAccount = getAccountByEmailAndType("customer@test.com", AccountType.CHECKING);
        validLimitsRequestDTO = new AccountLimitsRequestDTO(
                existingCheckingAccount.getIban(),
                AccountType.CHECKING, BigDecimal.valueOf(-1000), BigDecimal.valueOf(1000));
        invalidLimitsRequestDTO = new AccountLimitsRequestDTO(
                existingCheckingAccount.getIban(), AccountType.CHECKING,
                BigDecimal.valueOf(-1000), BigDecimal.valueOf(-1000)
        );
    }

    @Test
    @WithUserDetails(value = "customer@test.com")
    void getAccountsByUserId_customerSeesOwnAccounts_returnsAccountDetails() throws Exception {
        Account checkingAccount = getAccountByEmailAndType("customer@test.com", AccountType.CHECKING);
        Account savingsAccount = getAccountByEmailAndType("customer@test.com", AccountType.SAVINGS);
        Long userId = checkingAccount.getUser().getId();

        mockMvc.perform(get("/accounts/user")
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].iban").isNotEmpty())
                .andExpect(jsonPath("$[0].accountType").isNotEmpty())
                .andExpect(jsonPath("$[0].balance").isNumber())
                .andExpect(jsonPath("$[0].accountStatus").value("ACTIVE"))
                .andExpect(jsonPath("$[1].iban").isNotEmpty())
                .andExpect(jsonPath("$[1].accountStatus").value("ACTIVE"));
    }

    @Test
    void getAccountsByUserId_noToken_returns403() throws Exception {
        Account account = getAccountByEmailAndType("customer@test.com", AccountType.CHECKING);

        mockMvc.perform(get("/accounts/user")
                        .param("userId", account.getUser().getId().toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = "customer@test.com")
    void getIbanByName_validName_returns200() throws Exception {
        mockMvc.perform(get("/accounts/search")
                        .param("firstName", "Jane")
                        .param("lastName", "Doe"))
                .andExpect(status().isOk());
    }

    @Test
    void getIbanByName_noToken_returns403() throws Exception {
        mockMvc.perform(get("/accounts/search")
                        .param("firstName", "Jane")
                        .param("lastName", "Doe"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = "customer@test.com")
    void getAccountsByUserId_customerSeesOtherUserAccounts_returns403() throws Exception {
        Account otherAccount = getAccountByEmailAndType("insufficient@test.com", AccountType.CHECKING);
        Long otherUserId = otherAccount.getUser().getId();

        mockMvc.perform(get("/accounts/user")
                        .param("userId", otherUserId.toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = "employee@test.com")
    void update_returnsIsOk() throws Exception {
        mockMvc.perform(patch("/accounts/" + validLimitsRequestDTO.getIban())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLimitsRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.iban").value(validLimitsRequestDTO.getIban()))
                .andExpect(jsonPath("$.accountType").value(validLimitsRequestDTO.getAccountType().toString()))
                .andExpect(jsonPath("$.absoluteLimit")
                        .value(comparesEqualTo(validLimitsRequestDTO.getAbsoluteLimit()), BigDecimal.class))
                .andExpect(jsonPath("$.dailyLimit")
                        .value(comparesEqualTo(validLimitsRequestDTO.getDailyLimit()), BigDecimal.class));
    }

    @Test
    @WithUserDetails(value = "customer@test.com")
    void update_update_returnsForbiddenWhenUnauthorized() throws Exception {
        mockMvc.perform(patch("/accounts/" + validLimitsRequestDTO.getIban())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLimitsRequestDTO)))
                .andExpect(status().isForbidden());
    }

    // Springboot makes 401s appear as 403s by default
    @Test
    void update_returnsForbiddenWhenNoToken() throws Exception {
        mockMvc.perform(patch("/accounts/" + validLimitsRequestDTO.getIban())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLimitsRequestDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = "employee@test.com")
    void update_returnsBadRequestForInvalidPayload() throws Exception {
        mockMvc.perform(patch("/accounts/" + invalidLimitsRequestDTO.getIban())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidLimitsRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation Failed"))
                .andExpect(jsonPath("$.errors[0].field").value("dailyLimit"))
                .andExpect(jsonPath("$.errors[0].message").value("must be greater than or equal to 0"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    private Account getAccountByEmailAndType(String email, AccountType accountType) {
        for (Account a : accountRepository.findAll()) {
            if (a.getUser().getEmail().equals(email)
                    && a.getAccountType() == accountType) {
                return a;
            }
        }
        throw new IllegalStateException("No " + accountType + " account found for " + email);
    }

    @Test
    void getPaginatedAccounts() {
    }

    @Test
    void closeAccount() {
    }

    @Test
    void update() {
    }

    @Test
    void getAccountByIban() {
    }

}