package com.example.generation.repositories;

import com.example.generation.entities.Transaction;
import org.springframework.data.repository.CrudRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    //Jpa repository includes all the methods from Crud repo and Pagination and sorting repo

    @Query("SELECT t FROM Transaction t WHERE t.fromAccount.user.id = :userId OR t.toAccount.user.id = :userId")
    Page<Transaction> findTransactionsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.fromAccount.id = :id OR t.toAccount.id = :id")
    Page<Transaction> findByAccountId(@Param("id") Long accountId, Pageable pageable);
}
