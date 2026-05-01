package com.example.generation.services;

import com.example.generation.entities.User;
import com.example.generation.enums.UserStatus;
import com.example.generation.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
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

    public List<User> findByUserStatus(UserStatus userStatus) {
        return userRepository.findByUserStatus(userStatus);
    }

    public List<User> findUserByFirstNameAndLastName (String firstName, String lastName) {
        return userRepository.findUserByFirstNameAndLastName(firstName, lastName);
    }
}
