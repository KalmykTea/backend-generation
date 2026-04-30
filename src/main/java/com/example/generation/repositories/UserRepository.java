package com.example.generation.repositories;

import com.example.generation.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByBsnNumber(String bsnNumber);
    boolean existsByEmail(String email);
    boolean existsByBsnNumber(String bsnNumber);
}
