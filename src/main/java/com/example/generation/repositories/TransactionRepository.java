package com.example.generation.repositories;

import com.example.generation.entities.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    //Jpa repository includes all the methods from Crud repo and Pagination and sorting repo

    @Query("SELECT t FROM Transaction t WHERE t.fromAccount.user.id = :userId OR t.toAccount.user.id = :userId")
    Page<Transaction> findTransactionsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.fromAccount.iban = :iban OR t.toAccount.iban = :iban")
    Page<Transaction> findByAccountIBAN(@Param("iban") String accountIBAN, Pageable pageable);

    @Query("SELECT COALESCE(sum(t.amount), 0) FROM Transaction t " +
            "WHERE t.timestamp BETWEEN :startOfDay AND :endOfDay " +
            "AND t.transactionType IN ('WITHDRAWAL', 'TRANSFER')" +
            "AND t.fromAccount.iban = :iban"
    )
    BigDecimal getWithdrawalTotalWithinDurationByIban(
            @Param("iban") String iban,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );
}
