package com.example.generation.dtos.ResponseDTOs;

import com.example.generation.enums.AccountType;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class AccountLimitsResponseDTO {
    String iban;
    AccountType accountType;
    BigDecimal absoluteLimit;
    BigDecimal dailyLimit;
}
