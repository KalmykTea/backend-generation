package com.example.generation.services;

import com.example.generation.dtos.RequestDTOs.AccountFullRequestDTO;
import com.example.generation.dtos.ResponseDTOs.AccountFullResponseDTO;
import com.example.generation.entities.Account;
import com.example.generation.entities.User;
import com.example.generation.enums.AccountType;
import com.example.generation.mappers.ResponseDTOMappers.AccountFullResponseDTOMapper;
import com.example.generation.repositories.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountFullResponseDTOMapper accountFullResponseDTOMapper;

    public AccountService (
            AccountRepository accountRepository,
            AccountFullResponseDTOMapper accountFullResponseDTOMapper
    ) {
        this.accountRepository = accountRepository;
        this.accountFullResponseDTOMapper = accountFullResponseDTOMapper;
    }

    public Iterable<Account> findAll() {
        return accountRepository.findAll();
    }

    public List<Account> findAccountsByUserId(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    public AccountFullResponseDTO update(AccountFullRequestDTO accountFullRequestDTO, String iban) {
        Account existing = this.getAccountByIbanOrThrow(iban);
        existing.setDailyLimit(accountFullRequestDTO.getDailyLimit());
        existing.setAbsoluteLimit(accountFullRequestDTO.getAbsoluteLimit());
        return accountFullResponseDTOMapper.toDTO(accountRepository.save(existing));
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

    public void createAccountsForUser(User user) {
        Account current = new Account();
        current.setUser(user);
        current.setIban(generateUniqueIban());
        current.setAccountType(AccountType.CHECKING);
        accountRepository.save(current);

        Account savings = new Account();
        savings.setUser(user);
        savings.setIban(generateUniqueIban());
        savings.setAccountType(AccountType.SAVINGS);
        accountRepository.save(savings);
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
