package com.example.generation.dtos.RequestDTOs;

import com.example.generation.enums.TransactionType;
import com.example.generation.framework.groups.OnDeposit;
import com.example.generation.framework.groups.OnWithdrawal;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequestDTO {

    @Null
    private Long id;

    @Null(groups= OnDeposit.class)
    @Valid
    private AccountTransactionRequestDTO fromAccount;

    @Null(groups= OnWithdrawal.class)
    @Valid
    private AccountTransactionRequestDTO toAccount;

    @NotNull
    @Positive
    @Digits(integer = 12, fraction = 2)
    private BigDecimal amount;

    @Size(max = 140)
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
}
