package com.example.generation.services;

import com.example.generation.dtos.RequestDTOs.TransactionFilterRequest;
import com.example.generation.dtos.ResponseDTOs.TransactionSummaryResponse;
import com.example.generation.entities.Transaction;
import com.example.generation.entities.User;
import com.example.generation.repositories.TransactionRepository;
import com.example.generation.repositories.TransactionSpecification;
import com.example.generation.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public Page<TransactionSummaryResponse> getPaginatedTransactions(Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findAll(pageable);
        return transactions.map(this::convertToSummaryResponse);
    }

    public Page<TransactionSummaryResponse> getFilteredTransactionsForCustomer(String email, TransactionFilterRequest filters, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        Specification<Transaction> spec = TransactionSpecification.withFilters(filters, user.getId());
        Page<Transaction> transactions = transactionRepository.findAll(spec, pageable);
        return transactions.map(this::convertToSummaryResponse);
    }

    private TransactionSummaryResponse convertToSummaryResponse(Transaction transaction) {
        User initiator = transaction.getInitiatedBy();
        TransactionSummaryResponse.InitiatedByDTO initiatedBy = new TransactionSummaryResponse.InitiatedByDTO(
                initiator.getId(),
                initiator.getFirstName() + " " + initiator.getLastName(),
                initiator.getRole()
        );

        return new TransactionSummaryResponse(
                transaction.getId(),
                transaction.getTransactionType(),
                transaction.getFromAccount() != null ? transaction.getFromAccount().getIban() : null,
                transaction.getToAccount() != null ? transaction.getToAccount().getIban() : null,
                transaction.getAmount(),
                transaction.getTimestamp(),
                initiatedBy
        );
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
}
