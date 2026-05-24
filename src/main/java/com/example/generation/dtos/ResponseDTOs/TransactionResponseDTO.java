package com.example.generation.dtos.ResponseDTOs;

import com.example.generation.enums.TransactionType;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class TransactionResponseDTO {
    long id;
    String fromAccountIban;
    String toAccountIban;
    UserResponseDTO initiatedBy;
    BigDecimal amount;
    String description;
    TransactionType transactionType;
}
