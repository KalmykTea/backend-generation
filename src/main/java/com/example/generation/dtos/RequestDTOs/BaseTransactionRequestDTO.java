package com.example.generation.dtos.RequestDTOs;

import com.example.generation.enums.TransactionType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BaseTransactionRequestDTO {
    @Null
    private Long id;

    @NotNull
    @Positive
    @Digits(integer = 12, fraction = 2)
    private BigDecimal amount;

    @Size(max = 140)
    private String description;

    @NotNull
    private TransactionType transactionType;
}
