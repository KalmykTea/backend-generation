package com.example.generation.dtos.ResponseDTOs;

import com.example.generation.entities.User;
import com.example.generation.enums.AccountStatus;
import com.example.generation.enums.AccountType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AccountResponseDTO {
    private long id;
    private long userId;
    private String iban;
    private AccountType accountType;
    private BigDecimal balance;
    private BigDecimal absoluteLimit;
    private BigDecimal dailyLimit;
    private BigDecimal dailyTransfer;
}
