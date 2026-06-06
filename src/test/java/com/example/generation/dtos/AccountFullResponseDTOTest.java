package com.example.generation.dtos;

import com.example.generation.dtos.ResponseDTOs.AccountFullResponseDTO;
import com.example.generation.enums.AccountStatus;
import com.example.generation.enums.AccountType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountFullResponseDTOTest {

    @Test
    void AccountFullResponseDTO_BuilderAndGettersWork() {
        String iban = "NL62INHO0366278277";
        AccountType type = AccountType.SAVINGS;
        BigDecimal balance = BigDecimal.valueOf(1000);
        BigDecimal absoluteLimit = BigDecimal.valueOf(-500);
        BigDecimal dailyLimit = BigDecimal.valueOf(250);
        AccountStatus status = AccountStatus.ACTIVE;

        AccountFullResponseDTO dto = AccountFullResponseDTO.builder()
                .iban(iban)
                .accountType(type)
                .balance(balance)
                .absoluteLimit(absoluteLimit)
                .dailyLimit(dailyLimit)
                .accountStatus(status)
                .build();

        assertEquals(iban, dto.getIban());
        assertEquals(type, dto.getAccountType());
        assertEquals(balance, dto.getBalance());
        assertEquals(absoluteLimit, dto.getAbsoluteLimit());
        assertEquals(dailyLimit, dto.getDailyLimit());
        assertEquals(status, dto.getAccountStatus());
    }
}
