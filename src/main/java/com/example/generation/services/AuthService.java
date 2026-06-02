package com.example.generation.services;

import com.example.generation.dtos.RequestDTOs.LoginRequestDTO;
import com.example.generation.entities.Address;
import com.example.generation.entities.User;
import com.example.generation.framework.exceptions.EntityAlreadyExistsException;
import com.example.generation.repositories.AddressRepository;
import com.example.generation.repositories.UserRepository;
import com.example.generation.security.JwtProvider;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AddressRepository addressRepository;
    public AuthService (
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtProvider jwtProvider,
            AddressRepository addressRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.addressRepository = addressRepository;
    }

    public String login(LoginRequestDTO loginRequestDTO)
    {
        User user = userRepository.findByEmail(loginRequestDTO.getEmail()).orElseThrow(() -> new UsernameNotFoundException("Invalid username or password."));

        if(passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword()))
        {
            return  jwtProvider.generateToken(user.getEmail());
        }
        else
        {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
    }

    @Transactional
    public String register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EntityAlreadyExistsException("email", "Email already exists");
        }
        if (userRepository.existsByBsnNumber(user.getBsnNumber())) {
            throw new EntityAlreadyExistsException("bsnNumber", "BSN already exists");
        }

        Address address = user.getAddress();

        if (address != null && address.getId() == null) {
            Address savedAddress = addressRepository.save(address);
            user.setAddress(savedAddress);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return jwtProvider.generateToken(user.getEmail());
    }

}
