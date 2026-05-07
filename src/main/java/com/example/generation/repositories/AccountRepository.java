package com.example.generation.repositories;

import com.example.generation.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUser_Email(String email);
}
