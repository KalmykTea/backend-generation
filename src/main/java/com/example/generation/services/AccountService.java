package com.example.generation.services;

import com.example.generation.entities.Account;
import com.example.generation.repositories.AccountRepository;
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

    public Optional<Account> findById(Integer id) {
        return accountRepository.findById(id);
    }

    public Account save(Account account) {
        return accountRepository.save(account);
    }

    public Account update(Account account) {
        return accountRepository.save(account);
    }

    public void deleteById(Integer id) {
        accountRepository.deleteById(id);
    }
}
