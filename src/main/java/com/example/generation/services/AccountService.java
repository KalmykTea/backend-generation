package com.example.generation.services;

import com.example.generation.dtos.ResponseDTOs.AccountClosureResponse;
import com.example.generation.dtos.ResponseDTOs.AccountResponseDTO;
import com.example.generation.entities.Account;
import com.example.generation.entities.Transaction;
import com.example.generation.enums.AccountStatus;
import com.example.generation.framework.exceptions.AccountAlreadyClosedException;
import com.example.generation.framework.exceptions.AccountBalanceNotEmptyException;
import com.example.generation.mappers.ResponseDTOMappers.AccountResponseDTOMapper;
import com.example.generation.repositories.AccountRepository;
import com.example.generation.repositories.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AccountResponseDTOMapper accountResponseDTOMapper;

    public AccountService (
            AccountRepository accountRepository,
            TransactionRepository transactionRepository,
            AccountResponseDTOMapper accountResponseDTOMapper
    ) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.accountResponseDTOMapper = accountResponseDTOMapper;
    }

    // basic stuff, input custom logic according to your user stories
    public Iterable<Account> findAll() {
        return accountRepository.findAll();
    }

    public Account findById(Long id) {
        Optional<Account> account = accountRepository.findById(id);
        if (account.isPresent()) {
            return account.get();
        }
        else throw new EntityNotFoundException("Account with id: " + id + " not found");
    }

    public Account save(Account account) {
        return accountRepository.save(account);
    }

    public Account update(Account account, Long id) {
        Account existing = this.findById(id);
        if (account.getAbsoluteLimit()!= null) existing.setAbsoluteLimit(account.getAbsoluteLimit());
        if (account.getDailyLimit()!= null) existing.setDailyLimit(account.getDailyLimit());
        if (account.getAccountStatus()!= null) existing.setAccountStatus(account.getAccountStatus());
        return accountRepository.save(existing);
    }

    public Account withdrawOrDeposit(Long id, Transaction transaction) {
        Account account = this.findById(id);
        account.transact(transaction.getAmount(), transaction.getTransactionType());
        transactionRepository.save(transaction);
        accountRepository.save(account);
        return account;
    }

    public void deleteById(Long id) {
        accountRepository.deleteById(id);
    }

    public Page<AccountResponseDTO> getPaginatedAccounts(Pageable pageable) {
        return accountRepository.findAll(pageable)
                .map(this.accountResponseDTOMapper::toDTO);
    }

    public AccountClosureResponse closeAccount(Long accountId) {
        Account account = this.findById(accountId);

        if (account.getAccountStatus() == AccountStatus.CLOSED) {
            throw new AccountAlreadyClosedException("Account is already closed.");
        }

        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new AccountBalanceNotEmptyException("Account balance must be zero before closing.");
        }

        account.setAccountStatus(AccountStatus.CLOSED);
        accountRepository.save(account);
//Use maybe mapper???
        return new AccountClosureResponse(
                account.getId(),
                account.getIban(),
                account.getAccountStatus(),
                LocalDateTime.now(),
                "Account successfully closed."
        );
    }

}
