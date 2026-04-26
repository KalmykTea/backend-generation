package com.example.generation.dtos.RequestDTOs;

import com.example.generation.enums.TransactionType;
import com.example.generation.framework.annotations.ValidIBAN;
import com.example.generation.framework.groups.OnCreate;
import com.example.generation.framework.groups.OnUpdate;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequestDTO {

    @Null(groups = OnCreate.class, message = "ID must be null on creation")
    private Long id;

    @ValidIBAN(groups = {OnCreate.class, OnUpdate.class})
    private String fromAccountIBAN;

    @ValidIBAN(groups = {OnCreate.class, OnUpdate.class})
    private String toAccountIBAN;

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
