package com.example.generation.dtos;

import com.example.generation.dtos.ResponseDTOs.AccountIbanResponseDTO;
import com.example.generation.enums.AccountType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountIbanResponseDTOTest {

    @Test
    void AccountIbanResponseDTO_BuilderAndGettersWork() {
        String iban = "NL62INHO0366278277";
        AccountType type = AccountType.CHECKING;

        AccountIbanResponseDTO dto = AccountIbanResponseDTO.builder()
                .iban(iban)
                .accountType(type)
                .build();

        assertEquals(iban, dto.getIban());
        assertEquals(type, dto.getAccountType());
    }
}
