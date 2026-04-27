package com.example.generation.dtos.RequestDTOs;

import com.example.generation.enums.AccountType;
import com.example.generation.framework.annotations.ValidIBAN;
import com.example.generation.framework.groups.OnCreate;
import com.example.generation.framework.groups.OnUpdate;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountRequestDTO {

    @Null(groups = OnCreate.class, message = "ID must be null on creation")
    @NotNull(groups = OnUpdate.class, message = "ID is required for updates")
    private Long id;

    @NotNull(groups = OnCreate.class)
    @Positive(groups = OnCreate.class)
    private long userId;

    @ValidIBAN(groups = {OnCreate.class})
    private String iban;

    @NotNull(groups = {OnCreate.class})
    private AccountType accountType;

    @NotNull(groups = {OnCreate.class})
    @DecimalMax(value = "0", groups =  {OnCreate.class})
    private BigDecimal absoluteLimit;

    @NotNull(groups = {OnCreate.class})
    @PositiveOrZero(groups = {OnCreate.class})
    @DecimalMax(value = "0", groups =  {OnCreate.class})
    private BigDecimal dailyLimit;

    @NotNull(groups = {OnCreate.class})
    @PositiveOrZero(groups = {OnCreate.class})
    @DecimalMax(value = "0", groups =  {OnCreate.class})
    private BigDecimal dailyTransfer;
}
