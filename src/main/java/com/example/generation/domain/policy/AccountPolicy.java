package com.example.generation.domain.policy;

import com.example.generation.entities.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountPolicy {
    public void enforceAccountNotNull(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
    }
}
