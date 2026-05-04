package com.example.generation.dtos.ResponseDTOs;

import com.example.generation.enums.AccountType;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class AccountFullResponseDTO {
    String iban;
    long userId;
    AccountType accountType;
    BigDecimal balance;
    BigDecimal absoluteLimit;
    BigDecimal dailyLimit;
    BigDecimal dailyTransfer;
}
