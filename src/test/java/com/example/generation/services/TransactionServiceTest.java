package com.example.generation.services;

import com.example.generation.domain.policy.TransactionPolicy;
import com.example.generation.dtos.RequestDTOs.ATMRequestDTO;
import com.example.generation.dtos.RequestDTOs.TransactionFilterRequest;
import com.example.generation.dtos.ResponseDTOs.ATMResponseDTO;
import com.example.generation.dtos.ResponseDTOs.TransactionResponseDTO;
import com.example.generation.entities.Account;
import com.example.generation.entities.Transaction;
import com.example.generation.entities.User;
import com.example.generation.enums.TransactionType;
import com.example.generation.mappers.ResponseDTOMappers.ATMResponseDTOMapper;
import com.example.generation.mappers.ResponseDTOMappers.TransactionResponseDTOMapper;
import com.example.generation.repositories.AccountRepository;
import com.example.generation.repositories.TransactionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private TransactionPolicy transactionPolicy;

    @Mock
    private ATMResponseDTOMapper atmResponseDTOMapper;

    @Mock
    private TransactionResponseDTOMapper transactionResponseDTOMapper;

    @Mock
    private EntityManager entityManager;

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
    void processATMRequest_returnsATMResponseDTO() {
        when(accountService.getAccountByIbanOrThrow(atmRequestDTO.getIban()))
                .thenReturn(fromAccount);
        when(transactionRepository.getLast24HoursWithdrawalTotal(eq(fromAccount.getIban()), any()))
                .thenReturn(BigDecimal.ZERO);
        when(atmResponseDTOMapper.toDTO(any(Transaction.class)))
                .thenReturn(atmResponseDTO);
        doNothing().when(accountService).save(fromAccount);

        ATMResponseDTO result = transactionService.processATMRequest(atmRequestDTO);
        assertEquals(atmRequestDTO.getAmount(), result.getAmount());
        assertEquals(atmRequestDTO.getTransactionType(), result.getTransactionType());
        assertEquals(atmRequestDTO.getDescription(), result.getDescription());
        assertEquals(atmRequestDTO.getIban(), result.getIban());

        verify(accountService).getAccountByIbanOrThrow(fromAccount.getIban());
        verify(transactionPolicy).enforceValidATMTransaction(atmRequestDTO, fromAccount);
        verify(accountService).save(fromAccount);
        verify(transactionRepository).save(any(Transaction.class));

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
    void getFilteredTransactions_returnsPageOfTransactionResponseDTO() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        TransactionFilterRequest filters = new TransactionFilterRequest(
                LocalDate.now().minusDays(7),
                LocalDate.now(),
                null, null, null, null
        );

        Account account = new Account();
        account.setIban("NL01INHO0000000001");
        List<Account> userAccounts = List.of(account);

        when(accountRepository.findByUserId(userId)).thenReturn(userAccounts);

        Session session = mock(Session.class);
        Filter filter = mock(Filter.class);
        when(entityManager.unwrap(Session.class)).thenReturn(session);
        when(session.enableFilter(anyString())).thenReturn(filter);
        when(filter.setParameter(anyString(), any())).thenReturn(filter);
        when(filter.setParameterList(anyString(), any(List.class))).thenReturn(filter);

        Transaction transaction = new Transaction();
        Page<Transaction> transactionPage = new PageImpl<>(List.of(transaction));
        when(transactionRepository.findAll(pageable)).thenReturn(transactionPage);

        TransactionResponseDTO responseDTO = TransactionResponseDTO.builder().build();
        when(transactionResponseDTOMapper.toDTO(transaction)).thenReturn(responseDTO);

        Page<TransactionResponseDTO> result = transactionService.getFilteredTransactions(filters, pageable, userId);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(accountRepository).findByUserId(userId);
        verify(session).enableFilter("userAccountsFilter");
        verify(session).enableFilter("dateRangeFilter");
        verify(transactionRepository).findAll(pageable);
        verify(session).disableFilter("userAccountsFilter");
        verify(session).disableFilter("dateRangeFilter");
    }

    @Test
    void getFilteredTransactions_returnsEmptyPage_WhenNoAccounts() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        TransactionFilterRequest filters = new TransactionFilterRequest(null, null, null, null, null, null);

        when(accountRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        Page<TransactionResponseDTO> result = transactionService.getFilteredTransactions(filters, pageable, userId);

        assertTrue(result.isEmpty());
        verify(accountRepository).findByUserId(userId);
        verifyNoInteractions(entityManager, transactionRepository);
    }
}
