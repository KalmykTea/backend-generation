package com.example.generation.dtos;

import com.example.generation.enums.TransactionType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ATMDTO {
    private String iban;
    private BigDecimal amount;
    private String description;
    private TransactionType transactionType;
}
