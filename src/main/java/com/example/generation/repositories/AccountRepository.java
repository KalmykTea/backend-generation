package com.example.generation.repositories;

import com.example.generation.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findByIban(String iban);

    List<Account> findByUserFirstNameAndUserLastName(String firstName, String lastName);

    List<Account> findByUserId(Long userId);

    boolean existsByIbanAndUserId(String iban, Long userId);
}