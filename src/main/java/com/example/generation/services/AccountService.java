package com.example.generation.services;

import com.example.generation.dtos.RequestDTOs.AccountFullRequestDTO;
import com.example.generation.dtos.ResponseDTOs.AccountFullResponseDTO;
import com.example.generation.dtos.ResponseDTOs.AccountClosureResponse;
import com.example.generation.dtos.ResponseDTOs.AccountResponseDTO;
import com.example.generation.entities.Account;
import com.example.generation.entities.User;
import com.example.generation.enums.AccountType;
import com.example.generation.mappers.ResponseDTOMappers.AccountFullResponseDTOMapper;
import com.example.generation.entities.Transaction;
import com.example.generation.enums.AccountStatus;
import com.example.generation.framework.exceptions.AccountAlreadyClosedException;
import com.example.generation.framework.exceptions.AccountBalanceNotEmptyException;
import com.example.generation.mappers.ResponseDTOMappers.AccountResponseDTOMapper;
import com.example.generation.repositories.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountFullResponseDTOMapper accountFullResponseDTOMapper;
    private final TransactionRepository transactionRepository;
    private final AccountResponseDTOMapper accountResponseDTOMapper;

    public AccountService (
            AccountRepository accountRepository,
            AccountFullResponseDTOMapper accountFullResponseDTOMapper
            TransactionRepository transactionRepository,
            AccountResponseDTOMapper accountResponseDTOMapper
    ) {
        this.accountRepository = accountRepository;
        this.accountFullResponseDTOMapper = accountFullResponseDTOMapper;
        this.transactionRepository = transactionRepository;
        this.accountResponseDTOMapper = accountResponseDTOMapper;
    }

    public Iterable<Account> findAll() {
        return accountRepository.findAll();
    }

    public List<Account> findAccountsByUserId(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    public Account findById(Long id) {
        Optional<Account> account = accountRepository.findById(id);
        if (account.isPresent()) {
            return account.get();
        }
        else throw new EntityNotFoundException("Account with id: " + id + " not found");
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
