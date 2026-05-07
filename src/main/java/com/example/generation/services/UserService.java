package com.example.generation.services;

import com.example.generation.dtos.ResponseDTOs.UserFullResponseDTO;
import com.example.generation.dtos.ResponseDTOs.UserResponseDTO;
import com.example.generation.entities.User;
import com.example.generation.enums.UserStatus;
import com.example.generation.mappers.ResponseDTOMappers.UserFullResponseDTOMapper;
import com.example.generation.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final AccountService accountService;
    private final UserFullResponseDTOMapper userFullResponseDTOMapper;

    public UserService(
            UserRepository userRepository,
            AccountService accountService,
            UserFullResponseDTOMapper userFullResponseDTOMapper
    ){
        this.userRepository = userRepository;
        this.accountService = accountService;
        this.userFullResponseDTOMapper = userFullResponseDTOMapper;
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

    public UserFullResponseDTO approveUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // check if user status is Pending
        if (user.getUserStatus() != UserStatus.PENDING) {
            throw new EntityNotFoundException("User is not pending approval");
        }

        // change status to Approved, save, create accounts
        user.setUserStatus(UserStatus.APPROVED);
        User savedUser = userRepository.save(user);
        accountService.createAccountsForUser(user);

        return userFullResponseDTOMapper.toDTO(savedUser);
    }

    public List<UserFullResponseDTO> getPendingUsers() {
        return userRepository.findByUserStatus(UserStatus.PENDING)
                .stream()
                .map(userFullResponseDTOMapper::toDTO)
                .toList();
    }
}
