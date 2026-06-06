package com.example.generation.dtos;

import com.example.generation.dtos.ResponseDTOs.AccountClosureResponse;
import com.example.generation.enums.AccountStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountClosureResponseTest {

    @Test
    void AccountClosureResponse_ConstructorAndGettersWork() {
        String accountNumber = "NL62INHO0366278277";
        AccountStatus status = AccountStatus.CLOSED;
        LocalDateTime closedAt = LocalDateTime.now();
        String message = "Account closed successfully";

        AccountClosureResponse response = new AccountClosureResponse(accountNumber, status, closedAt, message);

        assertEquals(accountNumber, response.accountNumber());
        assertEquals(status, response.status());
        assertEquals(closedAt, response.closedAt());
        assertEquals(message, response.message());
    }
}
