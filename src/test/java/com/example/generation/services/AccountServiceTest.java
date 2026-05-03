package com.example.generation.services;

import com.example.generation.dtos.ResponseDTOs.EmployeeAccountResponseDTO;
import com.example.generation.entities.Account;
import com.example.generation.entities.User;
import com.example.generation.enums.AccountStatus;
import com.example.generation.enums.AccountType;
import com.example.generation.repositories.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private User testUser;
    private Account testAccount;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setFirstName("John");
        testUser.setLastName("Doe");

        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setIban("NL01INHO0000000001");
        testAccount.setUser(testUser);
        testAccount.setAccountType(AccountType.CURRENT);
        testAccount.setAccountStatus(AccountStatus.ACTIVE);
        testAccount.setBalance(new BigDecimal("100.00"));
    }

    @Test
    void getPaginatedAccounts_ShouldReturnPaginatedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Account> accountPage = new PageImpl<>(List.of(testAccount), pageable, 1);

        when(accountRepository.findAll(any(Pageable.class))).thenReturn(accountPage);

        Page<EmployeeAccountResponseDTO> result = accountService.getPaginatedAccounts(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        
        EmployeeAccountResponseDTO dto = result.getContent().get(0);
        assertEquals(testAccount.getId(), dto.accountId());
        assertEquals(testAccount.getIban(), dto.accountNumber());
        assertEquals("John Doe", dto.customerName());
        assertEquals(testAccount.getAccountType(), dto.accountType());
        assertEquals(testAccount.getAccountStatus(), dto.status());
        assertEquals(testAccount.getBalance(), dto.balance());
    }

    @Test
    void getPaginatedAccounts_ShouldReturnEmptyPage_WhenNoAccountsExist() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Account> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(accountRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        Page<EmployeeAccountResponseDTO> result = accountService.getPaginatedAccounts(pageable);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
    }
}
