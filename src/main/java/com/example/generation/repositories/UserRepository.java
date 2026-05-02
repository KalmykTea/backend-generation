package com.example.generation.repositories;

import com.example.generation.entities.User;
import com.example.generation.enums.UserStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {
    List<User> findByUserStatus(UserStatus userStatus);
}
