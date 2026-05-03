package com.example.generation.dtos.ResponseDTOs;

import com.example.generation.enums.Role;
import com.example.generation.enums.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionSummaryResponse(
    Long transactionId,
    TransactionType type,
    String fromAccount,
    String toAccount,
    BigDecimal amount,
    LocalDateTime timestamp,
    InitiatedByDTO initiatedBy
) {
    public record InitiatedByDTO(
        Long userId,
        String fullName,
        Role role
    ) {}
}
