package com.example.generation.services;

import com.example.generation.dtos.ResponseDTOs.AccountClosureResponse;
import com.example.generation.dtos.ResponseDTOs.EmployeeAccountResponseDTO;
import com.example.generation.entities.Account;
import com.example.generation.entities.Transaction;
import com.example.generation.enums.AccountStatus;
import com.example.generation.framework.exceptions.AccountAlreadyClosedException;
import com.example.generation.framework.exceptions.AccountBalanceNotEmptyException;
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

    public AccountService (
            AccountRepository accountRepository,
            TransactionRepository transactionRepository
    ) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    // basic stuff, input custom logic according to your user stories
    public Iterable<Account> findAll() {
        return accountRepository.findAll();
    }

    public Account findById(Long id) {
        Optional<Account> account = accountRepository.findById(Math.toIntExact(id));
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

    public void deleteById(Integer id) {
        accountRepository.deleteById(id);
    }

    public AccountClosureResponse closeAccount(Long accountId) {
        Account account = accountRepository.findById(Math.toIntExact(accountId))
                .orElseThrow(() -> new EntityNotFoundException("Account with ID " + accountId + " not found."));

        if (account.getAccountStatus() == AccountStatus.CLOSED) {
            throw new AccountAlreadyClosedException("Account is already closed.");
        }

        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            // Requirement: Closing an account with a non-zero balance should either be blocked with a 400 error or flagged with a warning.
            // Choice: Blocking with a 400 error to ensure all funds are settled before closure.
            throw new AccountBalanceNotEmptyException("Account balance must be zero before closing.");
        }

        account.setAccountStatus(AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());
        accountRepository.save(account);

        return new AccountClosureResponse(
                account.getId(),
                account.getIban(),
                account.getAccountStatus(),
                account.getClosedAt(),
                "Account successfully closed."
        );
    }

    public Page<EmployeeAccountResponseDTO> getPaginatedAccounts(Pageable pageable) {
        return accountRepository.findAll(pageable)
                .map(account -> new EmployeeAccountResponseDTO(
                        account.getId(),
                        account.getIban(),
                        account.getUser().getFirstName() + " " + account.getUser().getLastName(),
                        account.getAccountType(),
                        account.getAccountStatus(),
                        account.getBalance()
                ));
    }
}
