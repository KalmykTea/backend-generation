package com.example.generation.dtos;

import com.example.generation.dtos.ResponseDTOs.AccountLimitsResponseDTO;
import com.example.generation.enums.AccountType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountLimitsResponseDTOTest {

    @Test
    void AccountLimitsResponseDTO_BuilderAndGettersWork() {
        String iban = "NL62INHO0366278277";
        AccountType type = AccountType.CHECKING;
        BigDecimal absoluteLimit = BigDecimal.valueOf(-100);
        BigDecimal dailyLimit = BigDecimal.valueOf(500);

        AccountLimitsResponseDTO dto = AccountLimitsResponseDTO.builder()
                .iban(iban)
                .accountType(type)
                .absoluteLimit(absoluteLimit)
                .dailyLimit(dailyLimit)
                .build();

        assertEquals(iban, dto.getIban());
        assertEquals(type, dto.getAccountType());
        assertEquals(absoluteLimit, dto.getAbsoluteLimit());
        assertEquals(dailyLimit, dto.getDailyLimit());
    }
}
