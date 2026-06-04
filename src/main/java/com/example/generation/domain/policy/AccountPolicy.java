package com.example.generation.domain.policy;

import com.example.generation.dtos.RequestDTOs.AccountLimitsRequestDTO;
import com.example.generation.entities.Account;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AccountPolicy {
    public void enforceAccountNotNull(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account is null due to unexpected dto transaction type.");
        }
    }

    public void enforceDistinctAccountTypes(List<AccountLimitsRequestDTO> accountLimitsRequestDTOs) {
        long distinctTypes = accountLimitsRequestDTOs.stream()
                .map(AccountLimitsRequestDTO::getAccountType)
                .distinct()
                .count();

        if (distinctTypes != accountLimitsRequestDTOs.size()) {
            throw new IllegalArgumentException("Duplicate account types are not allowed");
        }
    }
}
