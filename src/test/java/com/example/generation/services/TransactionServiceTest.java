package com.example.generation.services;

import com.example.generation.domain.policy.TransactionPolicy;
import com.example.generation.dtos.RequestDTOs.ATMRequestDTO;
import com.example.generation.dtos.RequestDTOs.TransactionRequestDTO;
import com.example.generation.dtos.ResponseDTOs.ATMResponseDTO;
import com.example.generation.dtos.ResponseDTOs.TransactionResponseDTO;
import com.example.generation.entities.Account;
import com.example.generation.entities.Transaction;
import com.example.generation.entities.User;
import com.example.generation.enums.Role;
import com.example.generation.enums.TransactionType;
import com.example.generation.mappers.ResponseDTOMappers.ATMResponseDTOMapper;
import com.example.generation.mappers.ResponseDTOMappers.TransactionResponseDTOMapper;
import com.example.generation.repositories.AccountRepository;
import com.example.generation.repositories.TransactionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Mock
    private TransactionResponseDTOMapper transactionResponseDTOMapper;

    @InjectMocks
    private TransactionService transactionService;

    private ATMRequestDTO atmRequestDTO;
    private ATMResponseDTO atmResponseDTO;
    private Account fromAccount;

    private TransactionRequestDTO transferRequestDTO;
    private TransactionResponseDTO transferResponseDTO;
    private Account toAccount;

    private User currentUser;
    private User otherUser;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setRole(Role.CUSTOMER);

        otherUser = new User();
        otherUser.setId(2L);
        otherUser.setRole(Role.CUSTOMER);

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
        fromAccount.setUser(currentUser);
        fromAccount.setBalance(BigDecimal.valueOf(1000));

        toAccount = new Account();
        toAccount.setIban("NL32INHO0377278277");
        toAccount.setBalance(BigDecimal.valueOf(500));

        transferRequestDTO = new TransactionRequestDTO();
        transferRequestDTO.setFromAccountIban(fromAccount.getIban());
        transferRequestDTO.setToAccountIban(toAccount.getIban());
        transferRequestDTO.setAmount(BigDecimal.valueOf(100));
        transferRequestDTO.setDescription("test transfer");
        transferRequestDTO.setTransactionType(TransactionType.TRANSFER);

        transferResponseDTO = TransactionResponseDTO.builder()
                .fromAccountIban(fromAccount.getIban())
                .toAccountIban(toAccount.getIban())
                .build();

        // set authenticated user in security context
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(currentUser, null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    // clear user auth after each test
    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
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
    void processATMRequest_throwsAndPreventsPersist() {
        when(accountService.getAccountByIbanOrThrow(any())).thenReturn(fromAccount);
        doThrow(IllegalArgumentException.class).when(transactionPolicy).enforceValidATMTransaction(any(), any());
        assertThrows(IllegalArgumentException.class,
                () -> transactionService.processATMRequest(atmRequestDTO));
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void processTransfer_returnsTransactionResponseDTO() {
        LocalDate today = LocalDate.now();
        // tell mocks what to return
        when(accountService.getAccountByIbanOrThrow(transferRequestDTO.getFromAccountIban()))
                .thenReturn(fromAccount);
        when(accountService.getAccountByIbanOrThrow(transferRequestDTO.getToAccountIban()))
                .thenReturn(toAccount);
        // transact() calls this twice - once for fromAccount, once for toAccount
        when(transactionRepository.getWithdrawalTotalWithinDurationByIban(any(), any(), any()))
                .thenReturn(BigDecimal.ZERO);
        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(new Transaction());
        when(transactionResponseDTOMapper.toDTO(any(Transaction.class)))
                .thenReturn(transferResponseDTO);

        TransactionResponseDTO result = transactionService.processTransfer(transferRequestDTO);
        assertEquals(transferResponseDTO, result);
    }

    @Test
    void processTransfer_throwsEntityNotFoundExceptionWhenAccountNotFound() {
        when(accountService.getAccountByIbanOrThrow(any()))
                .thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class,
                () -> transactionService.processTransfer(transferRequestDTO));
        verify(transactionPolicy, never()).enforceCustomerOwnsFromAccount(any(), any());
    }

    @Test
    void processTransfer_throwsAccessDeniedExceptionWhenCustomerTransfersFromOtherAccount() {
        when(accountService.getAccountByIbanOrThrow(transferRequestDTO.getFromAccountIban()))
                .thenReturn(fromAccount);
        when(accountService.getAccountByIbanOrThrow(transferRequestDTO.getToAccountIban()))
                .thenReturn(toAccount);
        doThrow(AccessDeniedException.class)
                .when(transactionPolicy)
                .enforceCustomerOwnsFromAccount(any(), any());
        assertThrows(AccessDeniedException.class,
                () -> transactionService.processTransfer(transferRequestDTO));
    }

    @Test
    void findTransactionsByUserId_returnsPageFromRepository() {
        // prepare pageable and mock repository response
        Pageable pageable = PageRequest.of(0, 10);
        Page<Transaction> emptyPage = Page.empty();
        when(transactionRepository.findTransactionsByUserId(1L, pageable))
                .thenReturn(emptyPage);
        Page<Transaction> result = transactionService.findTransactionsByUserId(1L, pageable);
        assertEquals(emptyPage, result);
        verify(transactionRepository).findTransactionsByUserId(1L, pageable);
    }

    @Test
    void getTransactionsByAccountIBAN_returnsPageOfDTOs() {
        Pageable pageable = PageRequest.of(0, 10);
        when(accountService.getAccountByIbanOrThrow(fromAccount.getIban()))
                .thenReturn(fromAccount);
        when(transactionRepository.findByAccountIBAN(fromAccount.getIban(), pageable))
                .thenReturn(Page.empty());
        Page<TransactionResponseDTO> result = transactionService
                .getTransactionsByAccountIBAN(fromAccount.getIban(), pageable);
        verify(accountService).getAccountByIbanOrThrow(fromAccount.getIban());
        verify(transactionRepository).findByAccountIBAN(fromAccount.getIban(), pageable);
    }

    @Test
    void getTransactionsByAccountIBAN_throwsEntityNotFoundExceptionWhenAccountNotFound() {
        Pageable pageable = PageRequest.of(0, 10);
        when(accountService.getAccountByIbanOrThrow(fromAccount.getIban()))
                .thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class,
                () -> transactionService.getTransactionsByAccountIBAN(fromAccount.getIban(), pageable));
        verify(transactionRepository, never()).findByAccountIBAN(any(), any());
    }
}