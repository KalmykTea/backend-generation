package com.example.generation.controllers;

import com.example.generation.dtos.RequestDTOs.ATMRequestDTO;
import com.example.generation.entities.Account;
import com.example.generation.enums.AccountType;
import com.example.generation.enums.TransactionType;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class TransactionControllerFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    private Account account;

    @BeforeEach
    void setUp() {
        account = getAccountByEmailAndType("customer@test.com", AccountType.SAVINGS);
    }

    @Test
    @WithUserDetails(value = "customer@test.com")
    void deposit_persistsAndReturnsATMTransaction() throws Exception {
        ATMRequestDTO deposit = createATMRequest(
                account,
                BigDecimal.valueOf(100),
                "deposit transaction",
                TransactionType.DEPOSIT
        );

        performPostForATMRequest("/transactions/deposit", deposit)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.iban").value(deposit.getIban()))
                .andExpect(jsonPath("$.amount").value(deposit.getAmount()))
                .andExpect(jsonPath("$.description").value(deposit.getDescription()))
                .andExpect(jsonPath("$.transactionType").value(deposit.getTransactionType().name()));
    }

    @Test
    @WithUserDetails(value = "customer@test.com")
    void deposit_returnsForbiddenWhenUserDoesNotOwnAccount() throws Exception {
        ATMRequestDTO deposit = new ATMRequestDTO(
                "NL00INHO0000000000",
                BigDecimal.valueOf(100),
                "deposit transaction",
                TransactionType.DEPOSIT
        );

        performPostForATMRequest("/transactions/deposit", deposit)
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = "customer@test.com")
    void deposit_returnsBadRequestWhenDTOFailsValidation() throws Exception {
        ATMRequestDTO deposit = createATMRequest(
                account,
                BigDecimal.valueOf(-100),
                "deposit transaction",
                TransactionType.DEPOSIT
        );

        performPostForATMRequest("/transactions/withdraw", deposit)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation Failed"))
                .andExpect(jsonPath("$.errors[0].field").value("amount"))
                .andExpect(jsonPath("$.errors[0].message").value("must be greater than 0"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithUserDetails(value = "customer@test.com")
    void withdrawal_returnsBadRequestWhenDTOFailsValidation() throws Exception {
        ATMRequestDTO invalidWithdrawal = createATMRequest(
                account,
                BigDecimal.valueOf(-1000),
                "withdrawal transaction",
                TransactionType.WITHDRAWAL
        );

        performPostForATMRequest("/transactions/withdraw", invalidWithdrawal)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation Failed"))
                .andExpect(jsonPath("$.errors[0].field").value("amount"))
                .andExpect(jsonPath("$.errors[0].message").value("must be greater than 0"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithUserDetails(value = "customer@test.com")
    void withdrawal_returnsBadRequestWhenExceedingDailyLimit() throws Exception {
        ATMRequestDTO limitExceedingWithdrawal = createATMRequest(
                account,
                BigDecimal.valueOf(2001),
                "withdrawal transaction",
                TransactionType.WITHDRAWAL
        );

        performPostForATMRequest("/transactions/withdraw", limitExceedingWithdrawal)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Daily limit reached or transfer amount exceeds the daily limit."));
    }

    @Test
    @WithUserDetails(value = "insufficient@test.com")
    void withdrawal_returnsBadRequestWhenExceedingAbsoluteLimit() throws Exception {
        Account insufficient = getAccountByEmailAndType("insufficient@test.com", AccountType.CHECKING);

        ATMRequestDTO limitExceedingWithdrawal = createATMRequest(
                insufficient,
                BigDecimal.valueOf(11001),
                "withdrawal transaction",
                TransactionType.WITHDRAWAL
        );

        performPostForATMRequest("/transactions/withdraw", limitExceedingWithdrawal)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Balance cannot go below absolute limit"));
    }

    @Test
    @WithUserDetails(value = "customer@test.com")
    void withdrawal_persistsAndReturnsATMTransaction() throws Exception {
        ATMRequestDTO withdrawal = createATMRequest(
                account,
                BigDecimal.valueOf(100),
                "withdrawal transaction",
                TransactionType.WITHDRAWAL
        );

        performPostForATMRequest("/transactions/withdraw", withdrawal)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.iban").value(withdrawal.getIban()))
                .andExpect(jsonPath("$.amount").value(withdrawal.getAmount()))
                .andExpect(jsonPath("$.description").value(withdrawal.getDescription()))
                .andExpect(jsonPath("$.transactionType").value(withdrawal.getTransactionType().name()));
    }

    @Test
    @WithUserDetails(value = "customer@test.com")
    void withdrawal_returnsForbiddenWhenUserDoesNotOwnAccount() throws Exception {
        ATMRequestDTO withdrawal = new ATMRequestDTO(
                "NL00INHO0000000000",
                BigDecimal.valueOf(100),
                "withdrawal transaction",
                TransactionType.WITHDRAWAL
        );

        performPostForATMRequest("/transactions/withdraw", withdrawal)
                .andExpect(status().isForbidden());
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

    private ATMRequestDTO createATMRequest(
            Account account,
            BigDecimal amount,
            String description,
            TransactionType transactionType
    ) {
        return new ATMRequestDTO(
                account.getIban(),
                amount,
                description,
                transactionType
        );
    }

    private ResultActions performPostForATMRequest(String url, ATMRequestDTO request) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }
}