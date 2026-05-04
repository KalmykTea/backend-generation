package com.example.generation.services;

import com.example.generation.entities.Account;
import com.example.generation.entities.Transaction;
import com.example.generation.enums.AccountType;
import com.example.generation.enums.TransactionStatus;
import com.example.generation.enums.TransactionType;
import com.example.generation.repositories.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    public TransactionService(TransactionRepository transactionRepository, AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
    }

    // basic stuff, input custom logic according to your user stories
    public Iterable<Transaction> findAll(){
        return transactionRepository.findAll();
    }

    public Optional<Transaction> findById(Integer id){
        return transactionRepository.findById(id);
    }

    public Transaction save(Transaction transaction){
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction transferFunds(Long fromAccountId, Long toAccountId, BigDecimal amount, Long initiatedByUserId) {

        Account fromAccount = accountService.findById(fromAccountId);
        Account toAccount = accountService.findById(toAccountId);

        if (!fromAccount.getUser().getId().equals(toAccount.getUser().getId())) {
            if(fromAccount.getAccountType() != AccountType.CURRENT || toAccount.getAccountType() != AccountType.CURRENT)
            {
                throw new IllegalArgumentException("Both accounts need to be from type current");
            }
        }

        fromAccount.transact(amount, TransactionType.WITHDRAWAL);
        toAccount.transact(amount, TransactionType.DEPOSIT);

        accountService.save(fromAccount);
        accountService.save(toAccount);

        //**need to refactor this**
        Transaction transaction = new Transaction();
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setInitiatedBy(fromAccount.getUser());
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setStatus(TransactionStatus.COMPLETED);

        transaction.setId(null);
        return transactionRepository.save(transaction);
    }

    public Page<Transaction> findTransactionsByUserId(Long userId, Pageable pageable) {
        return transactionRepository.findTransactionsByUserId(userId, pageable);
    }
}
