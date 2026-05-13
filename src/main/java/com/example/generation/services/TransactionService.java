package com.example.generation.services;

import com.example.generation.dtos.RequestDTOs.ATMRequestDTO;
import com.example.generation.dtos.RequestDTOs.TransactionRequestDTO;
import com.example.generation.dtos.ResponseDTOs.ATMResponseDTO;
import com.example.generation.dtos.ResponseDTOs.TransactionResponseDTO;
import com.example.generation.entities.Account;
import com.example.generation.entities.Transaction;
import com.example.generation.enums.AccountStatus;
import com.example.generation.enums.AccountType;
import com.example.generation.enums.TransactionType;
import com.example.generation.mappers.ResponseDTOMappers.ATMResponseDTOMapper;
import com.example.generation.mappers.ResponseDTOMappers.TransactionResponseDTOMapper;
import com.example.generation.repositories.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final TransactionResponseDTOMapper transactionResponseDTOMapper;
    private final ATMResponseDTOMapper atmResponseDTOMapper;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountService accountService,
                              TransactionResponseDTOMapper transactionResponseDTOMapper, ATMResponseDTOMapper atmResponseDTOMapper) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
        this.transactionResponseDTOMapper = transactionResponseDTOMapper;
        this.atmResponseDTOMapper = atmResponseDTOMapper;
    }

    // basic stuff, input custom logic according to your user stories
    public Iterable<Transaction> findAll(){
        return transactionRepository.findAll();
    }

    public Optional<Transaction> findById(Long id){
        return transactionRepository.findById(id);
    }

    public Transaction save(Transaction transaction){
        return transactionRepository.save(transaction);
    }

    public Page<TransactionResponseDTO> getTransactionsByAccountIBAN(String accountIBAN, Pageable pageable) {
        accountService.getAccountByIbanOrThrow(accountIBAN);

        return transactionRepository
                .findByAccountIBAN(accountIBAN, pageable)
                .map(transactionResponseDTOMapper::toDTO);
    }

    @Transactional
    public TransactionResponseDTO processTransfer(TransactionRequestDTO dto) {
        Account fromAccount = accountService.getAccountByIbanOrThrow(dto.getFromAccount().getIban());
        validateAccountForTransfer(fromAccount, "Sender account");
        Account toAccount = accountService.getAccountByIbanOrThrow(dto.getToAccount().getIban());
        validateAccountForTransfer(toAccount, "Receiver account");

        if (!fromAccount.getUser().getId().equals(toAccount.getUser().getId())) {
            if (fromAccount.getAccountType() != AccountType.CHECKING || toAccount.getAccountType() != AccountType.CHECKING) {
                throw new IllegalArgumentException("Both accounts need to be of type CHECKING");
            }
        }

        fromAccount.transact(dto.getAmount(), TransactionType.TRANSFER);
        toAccount.transact(dto.getAmount(), TransactionType.DEPOSIT);
        accountService.save(fromAccount);
        accountService.save(toAccount);
        Transaction saved = transactionRepository.save(buildTransaction(fromAccount, toAccount, dto));
        return transactionResponseDTOMapper.toDTO(saved);
    }

    @Transactional
    public ATMResponseDTO processATMRequest(ATMRequestDTO dto) {
        Account account = accountService.getAccountByIbanOrThrow(dto.getIban());
        switch (dto.getTransactionType()) {
            case WITHDRAWAL:
                account.transact(dto.getAmount(), TransactionType.WITHDRAWAL);
                break;
            case DEPOSIT:
                account.transact(dto.getAmount(), TransactionType.DEPOSIT);
                break;
        }
        accountService.save(account);
        Transaction transaction = buildTransaction(account, dto);
        transactionRepository.save(transaction);
        return atmResponseDTOMapper.toDTO(transaction);
    }

    private void validateAccountForTransfer(Account account, String accountName) {
        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new IllegalArgumentException(accountName + " is not active");
        }
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

        return transaction;
    }

    //use functional programming later to merge the two build transactions... not sure how to do that yet
    private Transaction buildTransaction(Account account, ATMRequestDTO dto) {
        Transaction transaction = new Transaction();
        transaction.setFromAccount(account);
        transaction.setToAccount(account);
        transaction.setAmount(dto.getAmount());
        transaction.setDescription(dto.getDescription());
        transaction.setTransactionType(dto.getTransactionType());
        transaction.setInitiatedBy(account.getUser());
        return transaction;
    }

    public Page<Transaction> findTransactionsByUserId(Long userId, Pageable pageable) {
        return transactionRepository.findTransactionsByUserId(userId, pageable);
    }
}
