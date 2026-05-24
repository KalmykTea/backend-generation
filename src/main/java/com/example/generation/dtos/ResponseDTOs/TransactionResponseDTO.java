package com.example.generation.dtos.ResponseDTOs;

import com.example.generation.enums.TransactionType;
import lombok.Builder;
import lombok.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

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
