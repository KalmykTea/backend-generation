package com.example.generation.services;

import com.example.generation.dtos.RequestDTOs.TransactionRequestDTO;
import com.example.generation.dtos.ResponseDTOs.TransactionResponseDTO;
import com.example.generation.entities.Account;
import com.example.generation.entities.Transaction;
import com.example.generation.enums.AccountStatus;
import com.example.generation.enums.AccountType;
import com.example.generation.enums.TransactionType;
import com.example.generation.mappers.ResponseDTOMappers.TransactionResponseDTOMapper;
import com.example.generation.repositories.AccountRepository;
import com.example.generation.repositories.TransactionRepository;
import com.example.generation.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionResponseDTOMapper transactionResponseDTOMapper;
    private final EntityManager entityManager;
    private final UserRepository userRepository;
    private final AccountService accountService;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              UserRepository userRepository,
                              TransactionResponseDTOMapper transactionResponseDTOMapper,
                              EntityManager entityManager,
                              AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.transactionResponseDTOMapper = transactionResponseDTOMapper;
        this.entityManager = entityManager;
        this.accountService = accountService;
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponseDTO> getFilteredTransactions(
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal amountLt,
            BigDecimal amountGt,
            BigDecimal amountEq,
            String iban,
            Pageable pageable,
            Long userId
    ) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
        List<Account> userAccounts = accountRepository.findByUserId(userId);
        List<String> accountIbans = userAccounts.stream().map(Account::getIban).toList();

        if (accountIbans.isEmpty()) {
            return Page.empty(pageable);
        }

        Session session = enableFilters(startDate, endDate, amountLt, amountGt, amountEq, iban);
        session.enableFilter("userAccountsFilter").setParameterList("accountIbans", accountIbans);

        Page<Transaction> transactions = transactionRepository.findAll(pageable);
        cleanFilters(session);

        return transactions.map(transactionResponseDTOMapper::toDTO);
    }

    public Page<TransactionResponseDTO> getPaginatedTransactions(Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findAll(pageable);
        return transactions.map(transactionResponseDTOMapper::toDTO);
    }

    public Iterable<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    public Optional<Transaction> findById(Long id) {
        return transactionRepository.findById(id);
    }

    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Page<TransactionResponseDTO> getTransactionsByAccountIBAN(String accountIBAN, Pageable pageable) {
        accountService.getAccountByIbanOrThrow(accountIBAN);

        return transactionRepository
                .findByAccountIBAN(accountIBAN, pageable)
                .map(transactionResponseDTOMapper::toDTO);
    }

    @Transactional
    public TransactionResponseDTO processTransaction(TransactionRequestDTO dto, TransactionType type) {
        Account fromAccount = accountService.getAccountByIbanOrThrow(dto.getFromAccount().getIban());
        validateAccountForTransfer(fromAccount, "Sender account");

        Transaction transaction = switch (type) {
            case TRANSFER -> processTransfer(dto, fromAccount);
            case WITHDRAWAL -> processWithdraw(dto, fromAccount);
            case DEPOSIT -> processDeposit(dto, fromAccount);
        };

        Transaction saved = transactionRepository.save(transaction);
        return transactionResponseDTOMapper.toDTO(saved);
    }

    private Transaction processTransfer(TransactionRequestDTO dto, Account fromAccount) {
        Account toAccount = accountService.getAccountByIbanOrThrow(dto.getToAccount().getIban());
        validateAccountForTransfer(toAccount, "Receiver account");

        if (!fromAccount.getUser().getId().equals(toAccount.getUser().getId())) {
            if (fromAccount.getAccountType() != AccountType.CHECKING || toAccount.getAccountType() != AccountType.CHECKING) {
                throw new IllegalArgumentException("Both accounts need to be of type CHECKING");
            }
        }

        fromAccount.transact(dto.getAmount(), TransactionType.TRANSFER);
        toAccount.transact(dto.getAmount(), TransactionType.DEPOSIT);
        accountService.save(fromAccount);
        accountService.save(toAccount);
        return buildTransaction(fromAccount, toAccount, dto);
    }

    private Transaction processWithdraw(TransactionRequestDTO dto, Account fromAccount) {
        fromAccount.transact(dto.getAmount(), TransactionType.WITHDRAWAL);
        accountService.save(fromAccount);
        return buildTransaction(fromAccount, fromAccount, dto);
    }

    private Transaction processDeposit(TransactionRequestDTO dto, Account fromAccount) {
        fromAccount.transact(dto.getAmount(), TransactionType.DEPOSIT);
        accountService.save(fromAccount);
        return buildTransaction(fromAccount, fromAccount, dto);
    }

    private void validateAccountForTransfer(Account account, String accountName) {
        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new IllegalArgumentException(accountName + " is not active");
        }
    }

    private Transaction buildTransaction(Account fromAccount, Account toAccount, TransactionRequestDTO dto) {
        Transaction transaction = new Transaction();
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setAmount(dto.getAmount());
        transaction.setDescription(dto.getDescription());
        transaction.setTransactionType(dto.getTransactionType());
        transaction.setInitiatedBy(fromAccount.getUser());

        return transaction;
    }

    public Page<Transaction> findTransactionsByUserId(Long userId, Pageable pageable) {
        return transactionRepository.findTransactionsByUserId(userId, pageable);
    }

    private Session enableFilters(
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal amountLt,
            BigDecimal amountGt,
            BigDecimal amountEq,
            String iban
    ) {
        Session session = entityManager.unwrap(Session.class);

        if (startDate != null && endDate != null) {
            session.enableFilter("dateRangeFilter")
                    .setParameter("startDate", startDate.atStartOfDay())
                    .setParameter("endDate", endDate.atTime(23, 59, 59));
        }

        if (amountLt != null) {
            session.enableFilter("amountLtFilter").setParameter("amountLt", amountLt);
        }

        if (amountGt != null) {
            session.enableFilter("amountGtFilter").setParameter("amountGt", amountGt);
        }

        if (amountEq != null) {
            session.enableFilter("amountEqFilter").setParameter("amountEq", amountEq);
        }

        if (iban != null && !iban.isBlank()) {
            session.enableFilter("ibanFilter").setParameter("iban", "%" + iban + "%");
        }

        return session;
    }

    private void cleanFilters(Session session) {
        session.disableFilter("dateRangeFilter");
        session.disableFilter("amountLtFilter");
        session.disableFilter("amountGtFilter");
        session.disableFilter("amountEqFilter");
        session.disableFilter("ibanFilter");
        session.disableFilter("userAccountsFilter");
    }
}
