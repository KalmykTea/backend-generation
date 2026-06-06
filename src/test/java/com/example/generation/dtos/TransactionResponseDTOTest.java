package com.example.generation.dtos;

import com.example.generation.dtos.ResponseDTOs.TransactionResponseDTO;
import com.example.generation.dtos.ResponseDTOs.UserResponseDTO;
import com.example.generation.enums.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionResponseDTOTest {

    @Test
    void TransactionResponseDTO_BuilderAndGettersWork() {
        long id = 1L;
        String fromIban = "NL62INHO0366278277";
        String toIban = "NL62INHO0366278278";
        UserResponseDTO initiator = UserResponseDTO.builder().id(2L).firstName("John").lastName("Doe").build();
        BigDecimal amount = BigDecimal.valueOf(50);
        String description = "Lunch";
        TransactionType type = TransactionType.TRANSFER;
        LocalDateTime now = LocalDateTime.now();

        TransactionResponseDTO dto = TransactionResponseDTO.builder()
                .id(id)
                .fromAccountIban(fromIban)
                .toAccountIban(toIban)
                .initiatedBy(initiator)
                .amount(amount)
                .description(description)
                .transactionType(type)
                .timestamp(now)
                .build();

        assertEquals(id, dto.getId());
        assertEquals(fromIban, dto.getFromAccountIban());
        assertEquals(toIban, dto.getToAccountIban());
        assertEquals(initiator, dto.getInitiatedBy());
        assertEquals(amount, dto.getAmount());
        assertEquals(description, dto.getDescription());
        assertEquals(type, dto.getTransactionType());
        assertEquals(now, dto.getTimestamp());
    }
}
