package com.example.generation.dtos;

import com.example.generation.dtos.ResponseDTOs.ATMResponseDTO;
import com.example.generation.enums.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ATMResponseDTOTest {

    @Test
    void ATMResponseDTO_BuilderAndGettersWork() {
        String iban = "NL62INHO0366278277";
        BigDecimal amount = BigDecimal.valueOf(100);
        String description = "ATM Withdrawal";
        TransactionType type = TransactionType.WITHDRAWAL;

        ATMResponseDTO dto = ATMResponseDTO.builder()
                .iban(iban)
                .amount(amount)
                .description(description)
                .transactionType(type)
                .build();

        assertEquals(iban, dto.getIban());
        assertEquals(amount, dto.getAmount());
        assertEquals(description, dto.getDescription());
        assertEquals(type, dto.getTransactionType());
    }
}
