package com.example.generation.services;

import com.example.generation.dtos.RequestDTOs.TransactionRequestDTO;
import com.example.generation.dtos.ResponseDTOs.TransactionResponseDTO;
import com.example.generation.entities.Account;
import com.example.generation.dtos.RequestDTOs.TransactionFilterRequest;
import com.example.generation.dtos.ResponseDTOs.TransactionResponseDTO;
import com.example.generation.entities.Account;
import com.example.generation.entities.Transaction;
import com.example.generation.enums.AccountStatus;
import com.example.generation.enums.AccountType;
import com.example.generation.enums.TransactionType;
import com.example.generation.mappers.ResponseDTOMappers.TransactionResponseDTOMapper;
import com.example.generation.entities.User;
import com.example.generation.mappers.ResponseDTOMappers.TransactionResponseDTOMapper;
import com.example.generation.repositories.AccountRepository;
import com.example.generation.repositories.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.generation.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Page<TransactionResponseDTO> getFilteredTransactions(TransactionFilterRequest filters, Pageable pageable, Long userId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(email).orElseThrow();
        if (!user.getId().equals(userId) || !user.getRole().equals("EMPLOYEE")) {
            throw new IllegalArgumentException("User is not authorized to view transactions for this user.");
        }
        List<Account> userAccounts = accountRepository.findByUser_Email(email);
        List<Long> accountIds = userAccounts.stream().map(Account::getId).toList();

        if (accountIds.isEmpty()) {
            return Page.empty(pageable);
        }

        Session session = enableFilters(filters);

        // Use Specification to restrict to user's accounts
        Specification<Transaction> spec = (root, query, cb) ->
            cb.or(
                root.get("fromAccount").get("id").in(accountIds),
                root.get("toAccount").get("id").in(accountIds)
            );

        Page<Transaction> transactions = transactionRepository.findAll(spec, pageable);
        this.cleanFilters(session);

        return transactions.map(transactionResponseDTOMapper::toDTO);
    }

    public Page<TransactionResponseDTO> getPaginatedTransactions(Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findAll(pageable);
        return transactions.map(transactionResponseDTOMapper::toDTO);
    }

    // basic stuff, input custom logic according to your user stories
    public Iterable<Transaction> findAll(){
        return transactionRepository.findAll();
    }

    public Optional<Transaction> findById(Long id){
        return transactionRepository.findById(id);
    }

    public Transaction save(Transaction transaction){
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
            case TRANSFER  -> processTransfer(dto, fromAccount);
            case WITHDRAWAL  -> processWithdraw(dto, fromAccount);
            case DEPOSIT   -> processDeposit(dto, fromAccount);
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
        // Temporary - use sender as initiator
        transaction.setInitiatedBy(fromAccount.getUser());

        return transaction;
    }

    public Page<Transaction> findTransactionsByUserId(Long userId, Pageable pageable) {
        return transactionRepository.findTransactionsByUserId(userId, pageable);
    }

    private Session enableFilters(TransactionFilterRequest filters)
    {
        Session session = entityManager.unwrap(Session.class);

        // Hibernate dynamic filters for advanced filtering
        if (filters.startDate() != null && filters.endDate() != null) {
            session.enableFilter("dateRangeFilter")
                    .setParameter("startDate", filters.startDate().atStartOfDay())
                    .setParameter("endDate", filters.endDate().atTime(23, 59, 59));
        }

        if (filters.amountLt() != null) {
            session.enableFilter("amountLtFilter").setParameter("amountLt", filters.amountLt());
        }

        if (filters.amountGt() != null) {
            session.enableFilter("amountGtFilter").setParameter("amountGt", filters.amountGt());
        }

        if (filters.amountEq() != null) {
            session.enableFilter("amountEqFilter").setParameter("amountEq", filters.amountEq());
        }

        if (filters.iban() != null && !filters.iban().isBlank()) {
            session.enableFilter("ibanFilter").setParameter("iban", "%" + filters.iban() + "%");
        }

        return session;
    }

    private void cleanFilters(Session session)
    {
        session.disableFilter("dateRangeFilter");
        session.disableFilter("amountLtFilter");
        session.disableFilter("amountGtFilter");
        session.disableFilter("amountEqFilter");
        session.disableFilter("ibanFilter");
    }
}
