package com.example.generation.services;

import com.example.generation.entities.User;
import com.example.generation.enums.Role;
import com.example.generation.enums.UserStatus;
import com.example.generation.framework.exceptions.EntityAlreadyExistsException;
import com.example.generation.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Transactional
    public User register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EntityAlreadyExistsException("email", "Email already exists");
        }
        if (userRepository.existsByBsnNumber(user.getBsnNumber())) {
            throw new EntityAlreadyExistsException("bsnNumber", "BSN already exists");
        }

        return userRepository.save(user);
    }

    // basic stuff, input custom logic according to your user stories
    public Iterable<User> findAll(){
        return userRepository.findAll();
    }

    public Optional<User> findById(long id){
        return userRepository.findById(id);
    }

    public User save(User user){
        return userRepository.save(user);
    }

    public User update(User user){
        return userRepository.save(user);
    }

    public void deleteById(long id){
        userRepository.deleteById(id);
    }
}
