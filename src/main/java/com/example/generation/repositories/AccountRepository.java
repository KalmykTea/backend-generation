package com.example.generation.repositories;

import com.example.generation.entities.Account;
import com.example.generation.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account, Long> {
    Optional<Account> findByIban(String iban);
    List<Account> findByUser(User user);
}
