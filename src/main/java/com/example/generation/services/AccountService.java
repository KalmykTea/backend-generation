package com.example.generation.services;

import com.example.generation.dtos.ResponseDTOs.AccountResponseDTO;
import com.example.generation.entities.Account;
import com.example.generation.entities.User;
import com.example.generation.enums.AccountType;
import com.example.generation.framework.exceptions.EntityNotFoundException;
import com.example.generation.mappers.ResponseDTOMappers.AccountResponseDTOMapper;
import com.example.generation.repositories.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountResponseDTOMapper accountResponseDTOMapper;

    public AccountService (AccountRepository accountRepository, AccountResponseDTOMapper accountResponseDTOMapper) {
        this.accountRepository = accountRepository;
        this.accountResponseDTOMapper = accountResponseDTOMapper;
    }

    // basic stuff, input custom logic according to your user stories
    public Iterable<Account> findAll() {
        return accountRepository.findAll();
    }

    public Optional<Account> findById(Long id) {
        return accountRepository.findById(id);
    }

    public Account save(Account account) {
        return accountRepository.save(account);
    }

    public Account update(Account account) {
        return accountRepository.save(account);
    }

    public void deleteById(Long id) {
        accountRepository.deleteById(id);
    }

    public AccountResponseDTO getAccountByIban(String iban) {
        Account account = accountRepository.findByIban(iban)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        return accountResponseDTOMapper.toDTO(account);
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
        current.setAccountType(AccountType.CURRENT);
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
