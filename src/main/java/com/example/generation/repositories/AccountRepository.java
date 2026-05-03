package com.example.generation.repositories;

import com.example.generation.entities.Account;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AccountRepository extends CrudRepository<Account, Integer> {

    List<Account> findByUserId(Long userId);
}