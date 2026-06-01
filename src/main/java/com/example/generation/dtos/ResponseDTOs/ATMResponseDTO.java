package com.example.generation.dtos.ResponseDTOs;

import com.example.generation.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
@AllArgsConstructor
public class ATMResponseDTO {
    String iban;
    BigDecimal amount;
    String description;
    TransactionType transactionType;
}
