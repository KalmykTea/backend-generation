package com.example.generation.services;

import com.example.generation.domain.policy.AccountPolicy;
import com.example.generation.dtos.RequestDTOs.AccountLimitsRequestDTO;
import com.example.generation.dtos.ResponseDTOs.AccountLimitsResponseDTO;
import com.example.generation.entities.Account;
import com.example.generation.entities.User;
import com.example.generation.enums.AccountType;
import com.example.generation.mappers.ResponseDTOMappers.AccountFullResponseDTOMapper;
import com.example.generation.mappers.ResponseDTOMappers.AccountLimitsResponseDTOMapper;
import com.example.generation.repositories.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountLimitsResponseDTOMapper accountLimitsResponseDTOMapper;

    @Mock
    private AccountFullResponseDTOMapper accountFullResponseDTOMapper;

    @Mock
    private AccountPolicy accountPolicy;

    @Captor
    ArgumentCaptor<Account> accountCaptor;

    @Captor
    ArgumentCaptor<Collection<Account>> accountCollectionCaptor;

    @InjectMocks
    private AccountService accountService;

    String iban = "NL62INHO036278277";
    Account account = new Account();
    User user = new User();
    AccountLimitsRequestDTO checkingDTO = new AccountLimitsRequestDTO();
    AccountLimitsRequestDTO savingsDTO = new AccountLimitsRequestDTO();

    @BeforeEach
    void setUp() {
        user.setId(1L);
        checkingDTO.setIban(iban);
        checkingDTO.setAccountType(AccountType.CHECKING);
        checkingDTO.setDailyLimit(BigDecimal.valueOf(500));
        checkingDTO.setAbsoluteLimit(BigDecimal.valueOf(-100));
        savingsDTO.setIban(iban);
        savingsDTO.setAccountType(AccountType.SAVINGS);
        savingsDTO.setDailyLimit(BigDecimal.valueOf(200));
        savingsDTO.setAbsoluteLimit(BigDecimal.valueOf(-50));

        account.setIban(iban);
    }

    @Test
    void getAccountByIban_throwsExceptionWhenAccountNotFound() {
        String iban = "NL91NHO0417164300";
        when(accountRepository.findByIban(iban))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> accountService.getAccountByIban(iban));
        verify(accountFullResponseDTOMapper, never()).toDTO(any(Account.class));
    }

    @Test
    void update_updatesAccountLimits() {
        when(accountRepository.findByIban(iban)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);
        accountService.update(checkingDTO, iban);
        assertEquals(checkingDTO.getDailyLimit(), account.getDailyLimit());
        assertEquals(checkingDTO.getAbsoluteLimit(), account.getAbsoluteLimit());
        verify(accountRepository).findByIban(iban);
        verify(accountRepository).save(account);
    }

    @Test
    void update_DoesNotUpdateAccountWhenLimitsAreNull() {
        when(accountRepository.findByIban(iban)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);
        when(accountLimitsResponseDTOMapper.toDTO(any(Account.class)))
                .thenReturn(mock(AccountLimitsResponseDTO.class));
        checkingDTO.setDailyLimit(null);
        checkingDTO.setAbsoluteLimit(null);
        BigDecimal originalDailyLimit = account.getDailyLimit();
        BigDecimal originalAbsoluteLimit = account.getAbsoluteLimit();
        accountService.update(checkingDTO, iban);
        verify(accountRepository).save(accountCaptor.capture());
        assertEquals(originalDailyLimit, accountCaptor.getValue().getDailyLimit());
        assertEquals(originalAbsoluteLimit, accountCaptor.getValue().getAbsoluteLimit());
    }

    @Test
    void createAccountsForUser_appliesLimitsToCorrectAccounts() {
        accountService.createAccountsForUser(user, List.of(checkingDTO, savingsDTO));
        verify(accountRepository).saveAll(accountCollectionCaptor.capture());

        Collection<Account> saved = accountCollectionCaptor.getValue();
        Account checking = saved.stream().filter(a -> a.getAccountType() == AccountType.CHECKING).findFirst().orElseThrow();
        Account savings = saved.stream().filter(a -> a.getAccountType() == AccountType.SAVINGS).findFirst().orElseThrow();

        assertEquals(BigDecimal.valueOf(500), checking.getDailyLimit());
        assertEquals(BigDecimal.valueOf(-100), checking.getAbsoluteLimit());
        assertEquals(BigDecimal.valueOf(200), savings.getDailyLimit());
        assertEquals(BigDecimal.valueOf(-50), savings.getAbsoluteLimit());
        assertEquals(2, saved.size());
    }



}
