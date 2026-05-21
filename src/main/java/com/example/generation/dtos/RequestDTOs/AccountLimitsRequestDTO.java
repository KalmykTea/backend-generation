package com.example.generation.dtos.RequestDTOs;

import com.example.generation.enums.AccountType;
import com.example.generation.framework.annotations.ValidIBAN;
import com.example.generation.framework.groups.OnCreate;
import com.example.generation.framework.groups.OnUpdate;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AccountLimitsRequestDTO {

    @Null(groups = OnCreate.class)
    @ValidIBAN(groups = OnUpdate.class)
    private String iban;

    @NotNull(groups = OnCreate.class)
    private AccountType accountType;

    @NotNull(groups = { OnCreate.class, OnUpdate.class })
    private BigDecimal absoluteLimit;

    @NotNull(groups = { OnCreate.class, OnUpdate.class })
    @PositiveOrZero(groups = { OnCreate.class, OnUpdate.class })
    private BigDecimal dailyLimit;

}
