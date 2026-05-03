package com.example.generation.dtos.RequestDTOs;

import com.example.generation.enums.TransactionType;
import com.example.generation.framework.annotations.ValidIBAN;
import com.example.generation.framework.groups.OnCreate;
import com.example.generation.framework.groups.OnUpdate;
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

    @Valid
    private AccountRequestDTO fromAccount;

    @Valid
    private AccountRequestDTO toAccount;

    @Valid
    private UserRequestDTO initiatedBy;

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
