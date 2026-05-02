package com.example.generation.repositories;

import com.example.generation.entities.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByFromAccountIdOrToAccountId(Long fromAccountId, Long toAccountId, Pageable pageable);
}
