package com.example.generation.domain.policy;

import com.example.generation.entities.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AccountPolicyTest {
    Account account;
    Account nullAccount;
    AccountPolicy accountPolicy;

    @BeforeEach
    void setUp(){
        account = new Account();
        nullAccount = null;
        accountPolicy = new AccountPolicy();
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
}
