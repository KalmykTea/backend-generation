package com.example.generation.services;

import com.example.generation.dtos.ResponseDTOs.TransactionSummaryResponse;
import com.example.generation.entities.Account;
import com.example.generation.entities.Transaction;
import com.example.generation.entities.User;
import com.example.generation.enums.Role;
import com.example.generation.enums.TransactionType;
import com.example.generation.repositories.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private User employeeUser;
    private User customerUser;
    private Account fromAccount;
    private Account toAccount;

    @BeforeEach
    void setUp() {
        employeeUser = new User();
        employeeUser.setId(1L);
        employeeUser.setFirstName("Employee");
        employeeUser.setLastName("One");
        employeeUser.setRole(Role.EMPLOYEE);

        customerUser = new User();
        customerUser.setId(2L);
        customerUser.setFirstName("Customer");
        customerUser.setLastName("One");
        customerUser.setRole(Role.CUSTOMER);

        fromAccount = new Account();
        fromAccount.setIban("NL01INHO0000000001");

        toAccount = new Account();
        toAccount.setIban("NL01INHO0000000002");
    }

    @Test
    void getPaginatedTransactions_ShouldReturnMixedTransactionTypes() {
        Transaction t1 = new Transaction();
        t1.setId(1L);
        t1.setTransactionType(TransactionType.TRANSFER);
        t1.setFromAccount(fromAccount);
        t1.setToAccount(toAccount);
        t1.setAmount(new BigDecimal("100.00"));
        t1.setTimestamp(LocalDateTime.now());
        t1.setInitiatedBy(customerUser);

        Transaction t2 = new Transaction();
        t2.setId(2L);
        t2.setTransactionType(TransactionType.DEPOSIT);
        t2.setFromAccount(null);
        t2.setToAccount(toAccount);
        t2.setAmount(new BigDecimal("50.00"));
        t2.setTimestamp(LocalDateTime.now());
        t2.setInitiatedBy(employeeUser);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Transaction> transactionPage = new PageImpl<>(List.of(t1, t2), pageable, 2);

        when(transactionRepository.findAll(any(Pageable.class))).thenReturn(transactionPage);

        Page<TransactionSummaryResponse> result = transactionService.getPaginatedTransactions(pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());

        TransactionSummaryResponse dto1 = result.getContent().get(0);
        assertEquals(1L, dto1.transactionId());
        assertEquals(TransactionType.TRANSFER, dto1.type());
        assertEquals("NL01INHO0000000001", dto1.fromAccount());
        assertEquals("NL01INHO0000000002", dto1.toAccount());
        assertEquals(customerUser.getId(), dto1.initiatedBy().userId());
        assertEquals("Customer One", dto1.initiatedBy().fullName());
        assertEquals(Role.CUSTOMER, dto1.initiatedBy().role());

        TransactionSummaryResponse dto2 = result.getContent().get(1);
        assertEquals(2L, dto2.transactionId());
        assertEquals(TransactionType.DEPOSIT, dto2.type());
        assertNull(dto2.fromAccount());
        assertEquals("NL01INHO0000000002", dto2.toAccount());
        assertEquals(employeeUser.getId(), dto2.initiatedBy().userId());
        assertEquals("Employee One", dto2.initiatedBy().fullName());
        assertEquals(Role.EMPLOYEE, dto2.initiatedBy().role());
    }

    @Test
    void getPaginatedTransactions_ShouldReturnEmptyPage_WhenNoTransactionsExist() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Transaction> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(transactionRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        Page<TransactionSummaryResponse> result = transactionService.getPaginatedTransactions(pageable);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
    }
}
