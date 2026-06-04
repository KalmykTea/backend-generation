package com.example.generation.services;

import com.example.generation.domain.policy.TransactionPolicy;
import com.example.generation.dtos.RequestDTOs.ATMRequestDTO;
import com.example.generation.dtos.ResponseDTOs.ATMResponseDTO;
import com.example.generation.entities.Account;
import com.example.generation.entities.Transaction;
import com.example.generation.entities.User;
import com.example.generation.enums.TransactionType;
import com.example.generation.mappers.ResponseDTOMappers.ATMResponseDTOMapper;
import com.example.generation.repositories.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private TransactionPolicy transactionPolicy;

    @Mock
    private ATMResponseDTOMapper atmResponseDTOMapper;

    @InjectMocks
    private TransactionService transactionService;

    private ATMRequestDTO atmRequestDTO;
    private ATMResponseDTO atmResponseDTO;
    private Account fromAccount;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1L);
        String iban = "NL62INHO036278277";
        atmRequestDTO = new ATMRequestDTO(
                iban,
                BigDecimal.valueOf(100),
                "test transaction",
                TransactionType.DEPOSIT);
        atmResponseDTO = ATMResponseDTO.builder()
                .iban(iban)
                .amount(BigDecimal.valueOf(100))
                .description("test transaction")
                .transactionType(TransactionType.DEPOSIT)
                .build();
        fromAccount = new Account();
        fromAccount.setIban(iban);
    }

    @Test
    void processATMRequest_executesStepsInCorrectOrder() {
        LocalDate today = LocalDate.now();
        when(accountService.getAccountByIbanOrThrow(atmRequestDTO.getIban()))
                .thenReturn(fromAccount);
        when(transactionRepository.getWithdrawalTotalWithinDurationByIban(
                eq(fromAccount.getIban()),
                eq(today.atStartOfDay()),
                eq(today.atTime(LocalTime.MAX))))
                .thenReturn(BigDecimal.ZERO);
        when(atmResponseDTOMapper.toDTO(any(Transaction.class)))
                .thenReturn(atmResponseDTO);
        transactionService.processATMRequest(atmRequestDTO);
        verify(accountService).getAccountByIbanOrThrow(fromAccount.getIban());
        verify(transactionRepository).getWithdrawalTotalWithinDurationByIban(
                eq(fromAccount.getIban()),
                eq(today.atStartOfDay()),
                eq(today.atTime(LocalTime.MAX)));
        InOrder inOrder = Mockito.inOrder(transactionPolicy, accountService, transactionRepository);
        inOrder.verify(transactionPolicy).enforceValidATMTransaction(atmRequestDTO, fromAccount);
        inOrder.verify(accountService).save(fromAccount);
        inOrder.verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void processATMRequest_throwsEntityNotFoundExceptionWhenAccountNotFound() {
        when(accountService.getAccountByIbanOrThrow(any()))
                .thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class,
                () -> transactionService.processATMRequest(atmRequestDTO));
        verify(transactionPolicy, never()).enforceValidATMTransaction(any(), any());
    }

    @Test
    void processATMRequest_throwsAndPreventsPersist(){
        when(accountService.getAccountByIbanOrThrow(any())).thenReturn(fromAccount);
        doThrow(IllegalArgumentException.class).when(transactionPolicy).enforceValidATMTransaction(any(), any());
        assertThrows(IllegalArgumentException.class,
                () -> transactionService.processATMRequest(atmRequestDTO));
        verify(transactionRepository, never()).save(any());
    }
}
