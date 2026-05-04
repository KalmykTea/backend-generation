package com.example.generation.dtos.ResponseDTOs;

import com.example.generation.dtos.RequestDTOs.UserRequestDTO;
import com.example.generation.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class TransactionResponseDTO {
    long id;
    AccountTransactionResponseDTO fromAccount;
    AccountTransactionResponseDTO toAccount;
    UserResponseDTO initiatedBy;
    BigDecimal amount;
    String description;
    TransactionType transactionType;
}
