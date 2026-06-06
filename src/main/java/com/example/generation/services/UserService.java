package com.example.generation.services;

import com.example.generation.dtos.RequestDTOs.AccountLimitsRequestDTO;
import com.example.generation.dtos.ResponseDTOs.UserResponseDTO;
import com.example.generation.entities.User;
import com.example.generation.enums.UserStatus;
import com.example.generation.framework.exceptions.EntityAlreadyExistsException;
import com.example.generation.mappers.ResponseDTOMappers.UserResponseDTOMapper;
import com.example.generation.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final AccountService accountService;
    private final UserResponseDTOMapper userResponseDTOMapper;

    public UserService(
            UserRepository userRepository,
            AccountService accountService,
            UserResponseDTOMapper userResponseDTOMapper
    ){
        this.userRepository = userRepository;
        this.accountService = accountService;
        this.userResponseDTOMapper = userResponseDTOMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
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

    @Transactional
    public UserResponseDTO approveUser(Long id, List<AccountLimitsRequestDTO> accountLimitsRequestDTOS) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // check if user status is Pending
        if (user.getUserStatus() != UserStatus.PENDING) {
            throw new IllegalStateException("User is not pending approval");
        }

        // change status to Approved, save, create accounts
        user.setUserStatus(UserStatus.APPROVED);
        User savedUser = userRepository.save(user);
        accountService.createAccountsForUser(user, accountLimitsRequestDTOS);

        return userResponseDTOMapper.toDTO(savedUser);
    }

    public List<UserResponseDTO> getPendingUsers() {
        return userRepository.findByUserStatus(UserStatus.PENDING)
                .stream()
                .map(userResponseDTOMapper::toDTO)
                .toList();
    }


}
