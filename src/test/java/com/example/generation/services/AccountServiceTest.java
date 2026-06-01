package com.example.generation.services;

import com.example.generation.dtos.ResponseDTOs.AccountClosureResponse;
import com.example.generation.entities.Account;
import com.example.generation.enums.AccountStatus;
import com.example.generation.framework.exceptions.AccountAlreadyClosedException;
import com.example.generation.framework.exceptions.AccountBalanceNotEmptyException;
import com.example.generation.repositories.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private Account account;
    private final String iban = "NL01INHO0000000001";

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setIban(iban);
        account.setBalance(BigDecimal.ZERO);
        account.setAccountStatus(AccountStatus.ACTIVE);
    }

    @Test
    void closeAccount_Success() {
        when(accountRepository.findByIban(iban)).thenReturn(Optional.of(account));

        AccountClosureResponse response = accountService.closeAccount(iban);

        assertEquals(iban, response.accountNumber());
        assertEquals(AccountStatus.CLOSED, response.status());
        assertEquals("Account successfully closed.", response.message());
        assertEquals(AccountStatus.CLOSED, account.getAccountStatus());
        verify(accountRepository).save(account);
    }

    @Test
    void closeAccount_ThrowsException_WhenAccountNotFound() {
        when(accountRepository.findByIban(iban)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            accountService.closeAccount(iban);
        });

        verify(accountRepository, never()).save(any());
    }

    @Test
    void closeAccount_ThrowsException_WhenAccountAlreadyClosed() {
        account.setAccountStatus(AccountStatus.CLOSED);
        when(accountRepository.findByIban(iban)).thenReturn(Optional.of(account));

        AccountAlreadyClosedException exception = assertThrows(AccountAlreadyClosedException.class, () -> {
            accountService.closeAccount(iban);
        });
        System.out.println(exception.getMessage());
        assertEquals("Account is already closed.", exception.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void closeAccount_ThrowsException_WhenBalanceNotEmpty() {
        account.setBalance(new BigDecimal("10.00"));
        when(accountRepository.findByIban(iban)).thenReturn(Optional.of(account));

        AccountBalanceNotEmptyException exception = assertThrows(AccountBalanceNotEmptyException.class, () -> {
            accountService.closeAccount(iban);
        });

        assertEquals("Account balance must be zero before closing.", exception.getMessage());
        verify(accountRepository, never()).save(any());
    }
}
