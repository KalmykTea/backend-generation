package com.example.generation.dtos.ResponseDTOs;

import com.example.generation.enums.AccountStatus;
import com.example.generation.enums.AccountType;
import java.math.BigDecimal;

public record EmployeeAccountResponseDTO(
    Long accountId,
    String accountNumber,
    String customerName,
    AccountType accountType,
    AccountStatus status,
    BigDecimal balance
) {}
