package com.example.generation.dtos.RequestDTOs;

import com.example.generation.enums.AccountType;
import com.example.generation.framework.annotations.ValidIBAN;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AccountTransactionRequestDTO {
    @ValidIBAN
    private String iban;

    @NotNull
    private Long userId;

    @NotNull
    private AccountType accountType;

}
