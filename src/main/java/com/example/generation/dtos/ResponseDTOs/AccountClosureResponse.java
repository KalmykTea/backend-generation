package com.example.generation.dtos.ResponseDTOs;

import com.example.generation.enums.AccountStatus;
import java.time.LocalDateTime;

public record AccountClosureResponse(
    Long accountId,
    String accountNumber,
    AccountStatus status,
    LocalDateTime closedAt,
    String message
) {}
