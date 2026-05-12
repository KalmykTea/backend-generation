package com.example.generation.services;

import com.example.generation.dtos.RequestDTOs.LoginRequestDTO;
import com.example.generation.entities.User;
import com.example.generation.mappers.ResponseDTOMappers.AccountResponseDTOMapper;
import com.example.generation.repositories.AccountRepository;
import com.example.generation.repositories.TransactionRepository;
import com.example.generation.repositories.UserRepository;
import com.example.generation.security.JwtProvider;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public AuthService (
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtProvider jwtProvider
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    public String login(LoginRequestDTO loginRequestDTO)
    {
        User user = userRepository.findUserByEmail(loginRequestDTO.getEmail()).orElseThrow(() -> new UsernameNotFoundException("Invalid username or password."));

        if(passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword()))
        {
            return  jwtProvider.generateToken(user.getEmail());
        }
        else
        {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
    }

}
