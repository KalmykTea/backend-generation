package com.example.generation.dtos.RequestDTOs;

import com.example.generation.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountRequestDTO {

    @NotNull
    @Positive
    private long userId;

    @NotBlank
    @Pattern(
            regexp = "^NL\\d{2}[A-Z]{4}\\d{10}$",
            message = "Invalid Dutch IBAN format"
    )
    private String iban;

    @NotNull
    private AccountType accountType;

    @NotNull
    @Positive
    private BigDecimal absoluteLimit;

    @NotNull
    @Positive
    private BigDecimal dailyLimit;

    @NotNull
    @Positive
    private BigDecimal dailyTransfer;
}
