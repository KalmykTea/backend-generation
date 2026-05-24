package com.example.generation.services;

import com.example.generation.domain.policy.TransactionPolicy;
import com.example.generation.dtos.RequestDTOs.ATMRequestDTO;
import com.example.generation.dtos.RequestDTOs.TransactionRequestDTO;
import com.example.generation.dtos.ResponseDTOs.ATMResponseDTO;
import com.example.generation.dtos.ResponseDTOs.TransactionResponseDTO;
import com.example.generation.entities.Account;
import com.example.generation.dtos.RequestDTOs.TransactionFilterRequest;
import com.example.generation.entities.Transaction;
import com.example.generation.entities.User;
import com.example.generation.enums.Role;
import com.example.generation.enums.TransactionType;
import com.example.generation.mappers.ResponseDTOMappers.ATMResponseDTOMapper;
import com.example.generation.mappers.ResponseDTOMappers.TransactionResponseDTOMapper;
import com.example.generation.repositories.AccountRepository;
import com.example.generation.repositories.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.generation.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
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
    private final EntityManager entityManager;
    private final UserRepository userRepository;
    private final AccountService accountService;
    private final TransactionResponseDTOMapper transactionResponseDTOMapper;
    private final ATMResponseDTOMapper atmResponseDTOMapper;
    private final TransactionPolicy transactionPolicy;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountService accountService,
                              TransactionResponseDTOMapper transactionResponseDTOMapper,
                              ATMResponseDTOMapper atmResponseDTOMapper,
                              AccountRepository accountRepository,
                              UserRepository userRepository,
                              EntityManager entityManager,
                              TransactionPolicy transactionPolicy) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.entityManager = entityManager;
        this.accountService = accountService;
        this.transactionResponseDTOMapper = transactionResponseDTOMapper;
        this.atmResponseDTOMapper = atmResponseDTOMapper;
        this.transactionPolicy = transactionPolicy;
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
    public TransactionResponseDTO processTransfer(TransactionRequestDTO dto) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Account fromAccount = accountService.getAccountByIbanOrThrow(dto.getFromAccountIban());
        Account toAccount = accountService.getAccountByIbanOrThrow(dto.getToAccountIban());

        if (currentUser.getRole() == Role.CUSTOMER && !fromAccount.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You can only transfer from your own account");
        }

        transactionPolicy.enforceValidTransfer(fromAccount, toAccount, dto.getTransactionType());
        this.transact(fromAccount, dto.getAmount(), TransactionType.TRANSFER);
        this.transact(toAccount, dto.getAmount(), TransactionType.DEPOSIT);
        accountService.save(fromAccount);
        accountService.save(toAccount);
        Transaction saved = transactionRepository.save(buildTransaction(fromAccount, toAccount, dto, currentUser));
        return transactionResponseDTOMapper.toDTO(saved);
    }

    @Transactional
    public ATMResponseDTO processATMRequest(ATMRequestDTO dto) {
        Account account = accountService.getAccountByIbanOrThrow(dto.getIban());
        transactionPolicy.enforceValidATMTransaction(dto, account);
        this.transact(account, dto.getAmount(), dto.getTransactionType());
        accountService.save(account);
        Transaction transaction = buildTransaction(account, dto);
        transactionRepository.save(transaction);
        return atmResponseDTOMapper.toDTO(transaction);
    }

    private Transaction buildTransaction(Account fromAccount, Account toAccount, TransactionRequestDTO dto, User initiatedBy) {
        Transaction transaction = new Transaction();
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setAmount(dto.getAmount());
        transaction.setDescription(dto.getDescription());
        transaction.setTransactionType(dto.getTransactionType());
        transaction.setInitiatedBy(initiatedBy);

        return transaction;
    }

    //use functional programming later to merge the two build transactions... not sure how to do that yet
    private Transaction buildTransaction(Account account, ATMRequestDTO dto) {
        Transaction transaction = new Transaction();
        transaction.setFromAccount(account);
        transaction.setToAccount(account);
        transaction.setAmount(dto.getAmount());
        transaction.setDescription(dto.getDescription());
        transaction.setTransactionType(dto.getTransactionType());
        transaction.setInitiatedBy(account.getUser());
        return transaction;
    }

    private void transact(Account account, BigDecimal amount, TransactionType transactionType) {
        if (!LocalDate.now().equals(account.getLastTransferDate())) {
            account.setDailyTransfer(BigDecimal.ZERO);
            account.setLastTransferDate(LocalDate.now());
        }
        BigDecimal currentTransferTally = account.getDailyTransfer().add(amount);
        BigDecimal newBalance = transactionType == TransactionType.DEPOSIT
                ? account.getBalance().add(amount)
                : account.getBalance().subtract(amount);
        transactionPolicy.enforceDailyLimit(transactionType, account.getDailyLimit(), currentTransferTally);
        transactionPolicy.enforceAbsoluteLimit(account.getAbsoluteLimit(), newBalance);
        account.setDailyTransfer(currentTransferTally);
        account.setBalance(newBalance);
    }

    public Page<Transaction> findTransactionsByUserId(Long userId, Pageable pageable) {
        return transactionRepository.findTransactionsByUserId(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponseDTO> getFilteredTransactions(TransactionFilterRequest filters, Pageable pageable, Long userId) {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = this.userRepository.findById(userId).orElseThrow();
        if (!user.getId().equals(loggedInUser.getId())) {
            throw new IllegalArgumentException("User is not authorized to view transactions for this user.");
        }
        List<Account> userAccounts = accountRepository.findByUserId(userId);
        List<String> accountIbans = userAccounts.stream().map(Account::getIban).toList();

        if (accountIbans.isEmpty()) {
            return Page.empty(pageable);
        }

        Session session = enableFilters(filters);

        session.enableFilter("userAccountsFilter")
                .setParameterList("accountIbans", accountIbans);

        Page<Transaction> transactions = transactionRepository.findAll(pageable);
        this.cleanFilters(session);

        return transactions.map(transactionResponseDTOMapper::toDTO);
    }

    public Page<TransactionResponseDTO> getPaginatedTransactions(Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findAll(pageable);
        return transactions.map(transactionResponseDTOMapper::toDTO);
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

        return session;
    }

    private void cleanFilters(Session session)
    {
        session.disableFilter("dateRangeFilter");
        session.disableFilter("amountLtFilter");
        session.disableFilter("amountGtFilter");
        session.disableFilter("amountEqFilter");
        session.disableFilter("userAccountsFilter");
    }
}
