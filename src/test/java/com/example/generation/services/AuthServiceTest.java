package com.example.generation.services;

import com.example.generation.dtos.RequestDTOs.LoginRequestDTO;
import com.example.generation.entities.User;
import com.example.generation.repositories.UserRepository;
import com.example.generation.security.JwtProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_validCredentials_returnsToken() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("encodedPassword");

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("test@test.com");
        dto.setPassword("password123");

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtProvider.generateToken("test@test.com")).thenReturn("faketoken");

        String result = authService.login(dto);

        assertEquals("faketoken", result);
    }

    @Test
    void login_incorrectPassword_throwsUsernameNotFoundException() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("encodedPassword");

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("test@test.com");
        dto.setPassword("wrongpassword");

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        assertThrows(UsernameNotFoundException.class, () -> authService.login(dto));
    }

    @Test
    void login_incorrectEmail_throwsUsernameNotFoundException() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("wrong@test.com");
        dto.setPassword("password123");

        when(userRepository.findByEmail("wrong@test.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authService.login(dto));
    }
}
