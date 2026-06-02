package com.example.generation.controllers;

import com.example.generation.entities.Account;
import com.example.generation.enums.AccountType;
import com.example.generation.repositories.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Test
    @WithUserDetails(value = "customer@test.com")
    void getAccountsByUserId_customerSeesOwnAccounts_returns200() throws Exception {
        Account account = getAccountByEmailAndType("customer@test.com", AccountType.CHECKING);
        Long userId = account.getUser().getId();

        System.out.println("USER ID: " + userId);
        System.out.println("USER EMAIL: " + account.getUser().getEmail());

        mockMvc.perform(get("/accounts/user")
                        .param("userId", userId.toString()))
                .andExpect(status().isOk());
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
    @WithUserDetails(value = "customer@test.com")
    void getAccountsByUserId_customerSeesOtherUserAccounts_returns403() throws Exception {
        Account otherAccount = getAccountByEmailAndType("insufficient@test.com", AccountType.CHECKING);
        Long otherUserId = otherAccount.getUser().getId();

        mockMvc.perform(get("/accounts/user")
                        .param("userId", otherUserId.toString()))
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