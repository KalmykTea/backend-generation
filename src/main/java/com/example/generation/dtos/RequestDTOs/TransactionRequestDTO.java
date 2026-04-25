package com.example.generation.dtos.RequestDTOs;

import com.example.generation.enums.TransactionType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionRequestDTO {

    @NotBlank
    @Pattern(
            regexp = "^NL\\d{2}[A-Z]{4}\\d{10}$",
            message = "Invalid Dutch IBAN format"
    )
    private String fromAccountIBAN;

    @NotBlank
    @Pattern(
            regexp = "^NL\\d{2}[A-Z]{4}\\d{10}$",
            message = "Invalid Dutch IBAN format"
    )
    private String toAccountIBAN;

    @NotNull
    @Positive
    @Digits(integer = 12, fraction = 2)
    private BigDecimal amount;

    @Size(max = 140)
    private String description;

    @NotNull
    private TransactionType transactionType;
}
