package com.example.generation.domain.policy;

import com.example.generation.dtos.RequestDTOs.AccountLimitsRequestDTO;
import com.example.generation.entities.Account;
import com.example.generation.enums.AccountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AccountPolicyTest {
    Account account;
    Account nullAccount;
    AccountPolicy accountPolicy;
    List<AccountLimitsRequestDTO> accountLimitsRequestDTOs;
    List<AccountLimitsRequestDTO> invalidDTOlist;

    @BeforeEach
    void setUp(){
        account = new Account();
        nullAccount = null;
        accountPolicy = new AccountPolicy();
        invalidDTOlist = List.of(
                new AccountLimitsRequestDTO(null, AccountType.CHECKING, BigDecimal.valueOf(100), BigDecimal.valueOf(-100)),
                new AccountLimitsRequestDTO(null, AccountType.CHECKING, BigDecimal.valueOf(200), BigDecimal.valueOf(-100))
        );
        accountLimitsRequestDTOs = List.of(
                new AccountLimitsRequestDTO(null, AccountType.CHECKING, BigDecimal.valueOf(100), BigDecimal.valueOf(-100)),
                new AccountLimitsRequestDTO(null, AccountType.SAVINGS, BigDecimal.valueOf(200), BigDecimal.valueOf(-100))
        );
    }

    @Test
    void enforceAccountNotNull_throwsForNullAccount(){
        assertThrows(IllegalArgumentException.class, ()->
                accountPolicy.enforceAccountNotNull(nullAccount));
    }

    @Test
    void enforceAccountNotNull_allowsAccount(){
        assertDoesNotThrow(()->accountPolicy.enforceAccountNotNull(account));
    }

    @Test
    void enforceDistinctAccountTypes_throwsForDuplicateAccountTypes(){
        assertThrows(IllegalArgumentException.class,
                ()->accountPolicy.enforceDistinctAccountTypes(invalidDTOlist));
    }

    @Test
    void enforceDistinctAccountTypes_allowsDistinctAccountTypes(){
        assertDoesNotThrow(()->accountPolicy.enforceDistinctAccountTypes(accountLimitsRequestDTOs));
    }
}
