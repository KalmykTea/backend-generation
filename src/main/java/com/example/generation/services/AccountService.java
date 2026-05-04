package com.example.generation.services;

import com.example.generation.dtos.ResponseDTOs.AccountResponseDTO;
import com.example.generation.entities.Account;
import com.example.generation.entities.Transaction;
import com.example.generation.entities.User;
import com.example.generation.enums.AccountType;
import com.example.generation.mappers.ResponseDTOMappers.AccountResponseDTOMapper;
import com.example.generation.repositories.AccountRepository;
import com.example.generation.repositories.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

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
        Optional<Account> account = accountRepository.findById((long)Math.toIntExact(id));
        if (account.isPresent()) {
            return account.get();
        }
        else throw new EntityNotFoundException("Account with id: " + id + " not found");
    }

    public List<Account> findAccountsByUserId(Long userId) {
        return accountRepository.findByUserId(userId);
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

    public AccountResponseDTO getAccountByIban(String iban) {
        Account account = accountRepository.findByIban(iban)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        return accountResponseDTOMapper.toDTO(account);
    }

//    public AccountResponseDTO getAccountByIban(String iban, AccountStatus status = null) {
//        Account account = accountRepository.findByIban(iban)
//                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
    // if (!status) {
    //check if it equals status
    //return it if it does
    // }
//
//        return accountResponseDTOMapper.toDTO(account);
//    }

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
