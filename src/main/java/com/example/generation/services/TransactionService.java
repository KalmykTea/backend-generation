package com.example.generation.services;

import com.example.generation.dtos.RequestDTOs.TransactionRequestDTO;
import com.example.generation.dtos.ResponseDTOs.TransactionResponseDTO;
import com.example.generation.entities.Account;
import com.example.generation.entities.Transaction;
import com.example.generation.enums.AccountStatus;
import com.example.generation.enums.AccountType;
import com.example.generation.enums.TransactionType;
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

    public TransactionService(TransactionRepository transactionRepository,
                              AccountService accountService,
                              TransactionResponseDTOMapper transactionResponseDTOMapper) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
        this.transactionResponseDTOMapper = transactionResponseDTOMapper;
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
    public TransactionResponseDTO processTransaction(TransactionRequestDTO dto, TransactionType type) {
        Account fromAccount = accountService.getAccountByIbanOrThrow(dto.getFromAccount().getIban());
        validateAccountForTransfer(fromAccount, "Sender account");

        Transaction transaction = switch (type) {
            case TRANSFER  -> processTransfer(dto, fromAccount);
            case WITHDRAWAL  -> processWithdraw(dto, fromAccount);
            case DEPOSIT   -> processDeposit(dto, fromAccount);
        };

        Transaction saved = transactionRepository.save(transaction);
        return transactionResponseDTOMapper.toDTO(saved);
    }

    private Transaction processTransfer(TransactionRequestDTO dto, Account fromAccount) {
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
        return buildTransaction(fromAccount, toAccount, dto);
    }

    private Transaction processWithdraw(TransactionRequestDTO dto, Account fromAccount) {
        fromAccount.transact(dto.getAmount(), TransactionType.WITHDRAWAL);
        accountService.save(fromAccount);
        return buildTransaction(fromAccount, fromAccount, dto);
    }

    private Transaction processDeposit(TransactionRequestDTO dto, Account fromAccount) {
        fromAccount.transact(dto.getAmount(), TransactionType.DEPOSIT);
        accountService.save(fromAccount);
        return buildTransaction(fromAccount, fromAccount, dto);
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

    public Page<Transaction> findTransactionsByUserId(Long userId, Pageable pageable) {
        return transactionRepository.findTransactionsByUserId(userId, pageable);
    }
}
