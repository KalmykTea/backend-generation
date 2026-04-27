package com.example.generation.services;

import com.example.generation.entities.Account;
import com.example.generation.repositories.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService (AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
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
        return save(existing);
    }

    public void deleteById(Integer id) {
        accountRepository.deleteById(id);
    }
}
