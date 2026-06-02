package com.example.generation.dtos.RequestDTOs;

import com.example.generation.enums.TransactionType;
import com.example.generation.framework.annotations.ValidIBAN;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ATMRequestDTO extends BaseTransactionRequestDTO {
    @ValidIBAN
    private String iban;

    public ATMRequestDTO(String iban, BigDecimal amount, String description, TransactionType transactionType) {
        super(null, amount, description, transactionType);
        this.iban = iban;
    }
}
