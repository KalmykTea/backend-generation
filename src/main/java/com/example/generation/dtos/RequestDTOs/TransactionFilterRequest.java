package com.example.generation.dtos.RequestDTOs;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionFilterRequest(
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal amountLt,
    BigDecimal amountGt,
    BigDecimal amountEq,
    String iban
) {}
