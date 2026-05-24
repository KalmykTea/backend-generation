package com.example.generation.services;

import com.example.generation.domain.policy.AccountPolicy;
import com.example.generation.dtos.RequestDTOs.AccountLimitsRequestDTO;
import com.example.generation.dtos.ResponseDTOs.AccountFullResponseDTO;
import com.example.generation.dtos.ResponseDTOs.AccountLimitsResponseDTO;
import com.example.generation.entities.Account;
import com.example.generation.entities.User;
import com.example.generation.enums.AccountType;
import com.example.generation.mappers.ResponseDTOMappers.AccountFullResponseDTOMapper;
import com.example.generation.mappers.ResponseDTOMappers.AccountLimitsResponseDTOMapper;
import com.example.generation.repositories.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountLimitsResponseDTOMapper accountLimitsResponseDTOMapper;
    private final AccountFullResponseDTOMapper accountFullResponseDTOMapper;
    private final AccountPolicy accountPolicy;

    public AccountService (
            AccountRepository accountRepository,
            AccountLimitsResponseDTOMapper accountLimitsResponseDTOMapper,
            AccountFullResponseDTOMapper accountFullResponseDTOMapper,
            AccountPolicy accountPolicy
    ) {
        this.accountRepository = accountRepository;
        this.accountLimitsResponseDTOMapper = accountLimitsResponseDTOMapper;
        this.accountFullResponseDTOMapper = accountFullResponseDTOMapper;
        this.accountPolicy = accountPolicy;
    }

    public Iterable<Account> findAll() {
        return accountRepository.findAll();
    }

    public List<Account> findAccountsByUserId(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    public AccountLimitsResponseDTO update(AccountLimitsRequestDTO accountLimitsRequestDTO, String iban) {
        Account existing = this.getAccountByIbanOrThrow(iban);
        if (accountLimitsRequestDTO.getDailyLimit() != null) {
            existing.setDailyLimit(accountLimitsRequestDTO.getDailyLimit());
        }
        if (accountLimitsRequestDTO.getAbsoluteLimit() != null) {
            existing.setAbsoluteLimit(accountLimitsRequestDTO.getAbsoluteLimit());
        }
        return accountLimitsResponseDTOMapper.toDTO(accountRepository.save(existing));
    }

    public void save(Account account) {
        accountRepository.save(account);
    }

    public Account getAccountByIbanOrThrow(String iban) {
        return accountRepository.findByIban(iban)
                .orElseThrow(() -> new EntityNotFoundException("Account with IBAN " + iban + " not found"));
    }

    public void deleteByIban(String iban) {
        accountRepository.deleteById(iban);
    }

    public AccountFullResponseDTO getAccountByIban(String iban) {
        Account account = accountRepository.findByIban(iban)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        return accountFullResponseDTOMapper.toDTO(account);
    }

    public List<String> getIbansByUserName(String firstName, String lastName) {
        return accountRepository
                .findByUserFirstNameAndUserLastName(firstName, lastName)
                .stream()
                .map(Account::getIban)
                .toList();
    }

    public void createAccountsForUser(User user, List<AccountLimitsRequestDTO> accountLimitsRequestDTOs) {
        Map<AccountType, Account> accountMap = Map.of(
                AccountType.CHECKING, createAccount(user, AccountType.CHECKING),
                AccountType.SAVINGS, createAccount(user, AccountType.SAVINGS)
        );

        for (AccountLimitsRequestDTO dto : accountLimitsRequestDTOs) {
            Account account = accountMap.get(dto.getAccountType()); //get the account that matches the account type
            accountPolicy.enforceAccountNotNull(account); //map will return null if the key (account type <> checking or savings) doesn't exist.
            account.setDailyLimit(dto.getDailyLimit());
            account.setAbsoluteLimit(dto.getAbsoluteLimit());
        }

        accountRepository.saveAll(accountMap.values());
    }

    private Account createAccount(User user, AccountType type) {
        Account account = new Account();
        account.setUser(user);
        account.setIban(generateUniqueIban());
        account.setAccountType(type);
        return account;
    }

    private String generateUniqueIban() {
        Random random = new Random();
        String iban;

        do {
            // Build IBAN string (NL + 00 + INHO0 + 000000000)
            StringBuilder sb = new StringBuilder();
            int number = random.nextInt(10,100);
            sb.append("NL");
            sb.append(number);
            sb.append("INHO0");
            for (int i = 0; i < 9; i++) {
                sb.append(random.nextInt(10));
            }
            iban = sb.toString();
        } while (accountRepository.findByIban(iban).isPresent());

        return iban;
    }
}
