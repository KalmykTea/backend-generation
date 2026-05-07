package com.example.generation.dtos.RequestDTOs;

import com.example.generation.enums.AccountType;
import com.example.generation.framework.annotations.ValidIBAN;
import com.example.generation.framework.groups.OnCreate;
import com.example.generation.framework.groups.OnUpdate;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountFullRequestDTO {

    @Null(groups = OnCreate.class)
    @ValidIBAN(groups = OnUpdate.class)
    private String iban;

    private Long userId;

    @NotNull(groups = OnCreate.class)
    private AccountType accountType;

    @NotNull(groups = { OnCreate.class, OnUpdate.class })
    private BigDecimal absoluteLimit;

    @NotNull(groups = { OnCreate.class, OnUpdate.class })
    @PositiveOrZero(groups = { OnCreate.class, OnUpdate.class })
    private BigDecimal dailyLimit;

    @NotNull(groups = OnCreate.class)
    @PositiveOrZero(groups = OnCreate.class)
    private BigDecimal dailyTransfer;
}
