package com.example.generation.dtos.ResponseDTOs;

import com.example.generation.enums.AccountType;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AccountTransactionResponseDTO {
    String iban;
    long userId;
    AccountType accountType;
}
