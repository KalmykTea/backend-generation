package com.example.generation.services;

import com.example.generation.dtos.RequestDTOs.TransactionRequestDTO;
import com.example.generation.dtos.ResponseDTOs.TransactionResponseDTO;
import com.example.generation.entities.Account;
import com.example.generation.entities.Transaction;
import com.example.generation.enums.AccountStatus;
import com.example.generation.enums.AccountType;
import com.example.generation.framework.exceptions.EntityNotFoundException;
import com.example.generation.mappers.ResponseDTOMappers.TransactionResponseDTOMapper;
import com.example.generation.repositories.AccountRepository;
import com.example.generation.repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionResponseDTOMapper transactionResponseDTOMapper;

    public TransactionService(TransactionRepository transactionRepository, TransactionResponseDTOMapper transactionResponseDTOMapper, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.transactionResponseDTOMapper = transactionResponseDTOMapper;
    }

    // basic stuff, input custom logic according to your user stories
    public Iterable<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    public Optional<Transaction> findById(Long id) {
        return transactionRepository.findById(id);
    }

    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Page<TransactionResponseDTO> getTransactionsByAccountId(Long accountId, Pageable pageable) {
        accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        return transactionRepository
                .findByFromAccountIdOrToAccountId(accountId, accountId, pageable)
                .map(transactionResponseDTOMapper::toDTO);
    }

    // All steps must succeed together — if anything fails, all changes rolled back
    @Transactional
    public TransactionResponseDTO createTransaction(TransactionRequestDTO dto) {
        Account fromAccount = getAccountByIbanOrThrow(dto.getFromAccountIBAN());
        Account toAccount = getAccountByIbanOrThrow(dto.getToAccountIBAN());

        // Validation
        validateAccountForTransfer(fromAccount, "Sender account");
        validateAccountForTransfer(toAccount, "Receiver account");
        validateTransferLimits(fromAccount, dto.getAmount());

        applyTransfer(fromAccount, toAccount, dto.getAmount());

        Transaction saved = buildTransaction(fromAccount, toAccount, dto);

        return transactionResponseDTOMapper.toDTO(saved);
    }

    private Account getAccountByIbanOrThrow(String iban) {
        return accountRepository.findByIban(iban)
                .orElseThrow(() -> new EntityNotFoundException("Account with IBAN " + iban + " not found"));
    }

    private void validateAccountForTransfer(Account account, String accountName) {
        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new IllegalArgumentException(accountName + " is not active");
        }
        if (account.getAccountType() != AccountType.CURRENT) {
            throw new IllegalArgumentException(accountName + " is not a current account");
        }
    }

    private void validateTransferLimits(Account fromAccount, BigDecimal amount) {
        // Check if there's enough balance for transaction
        if (fromAccount.getBalance().subtract(amount).compareTo(fromAccount.getAbsoluteLimit()) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        // Check if daily limit is exceeded
        if (fromAccount.getDailyTransfer().add(amount).compareTo(fromAccount.getDailyLimit()) > 0) {
            throw new IllegalArgumentException("Daily limit exceeded");
        }
    }

    private void applyTransfer(Account fromAccount, Account toAccount, BigDecimal amount) {
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));
        fromAccount.setDailyTransfer(fromAccount.getDailyTransfer().add(amount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
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

        return transactionRepository.save(transaction);
    }
}
