package com.example.generation.dtos.RequestDTOs;

import com.example.generation.enums.AccountType;
import com.example.generation.framework.annotations.ValidIBAN;
import com.example.generation.framework.groups.OnCreate;
import com.example.generation.framework.groups.OnUpdate;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountRequestDTO {

    @Null(groups = OnCreate.class, message = "ID must be null on creation")
    @NotNull(groups = OnUpdate.class, message = "ID is required for updates")
    private Long id;

    @NotNull
    @Positive
    private long userId;

    @ValidIBAN(groups = {OnCreate.class, OnUpdate.class})
    private String iban;

    @NotNull(groups = {OnCreate.class, OnUpdate.class})
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @NotNull
    @PositiveOrZero
    @DecimalMax("0")
    private BigDecimal absoluteLimit;

    @NotNull
    @PositiveOrZero
    @DecimalMax("0")
    private BigDecimal dailyLimit;

    @NotNull
    @PositiveOrZero
    private BigDecimal dailyTransfer;
}
