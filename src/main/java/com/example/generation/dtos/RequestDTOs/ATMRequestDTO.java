package com.example.generation.dtos.RequestDTOs;

import com.example.generation.enums.TransactionType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ATMRequestDTO {
    @NotBlank
    private String iban;

    @NotNull
    @Positive
    @Digits(integer = 12, fraction = 2)
    private BigDecimal amount;

    @Size(max = 140)
    private String description;

    @NotNull
    private TransactionType transactionType;
}
