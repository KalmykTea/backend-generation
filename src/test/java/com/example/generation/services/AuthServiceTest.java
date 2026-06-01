package com.example.generation.services;

import com.example.generation.entities.Address;
import com.example.generation.entities.User;
import com.example.generation.framework.exceptions.EntityAlreadyExistsException;
import com.example.generation.repositories.AddressRepository;
import com.example.generation.repositories.UserRepository;
import com.example.generation.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AuthService authService;

    private User user;
    private Address address;

    @BeforeEach
    void setUp() {
        address = new Address();
        address.setCity("Amsterdam");

        user = new User();
        user.setEmail("test@example.com");
        user.setBsnNumber("123456789");
        user.setPassword("password");
        user.setAddress(address);
    }

    @Test
    void register_Success() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByBsnNumber(user.getBsnNumber())).thenReturn(false);
        when(addressRepository.save(any(Address.class))).thenReturn(address);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(jwtProvider.generateToken(user.getEmail())).thenReturn("mockedToken");

        String token = authService.register(user);

        assertEquals("mockedToken", token);
        assertEquals("encodedPassword", user.getPassword());
        verify(userRepository).existsByEmail(user.getEmail());
        verify(userRepository).existsByBsnNumber(user.getBsnNumber());
        verify(addressRepository).save(address);
        verify(passwordEncoder).encode("password");
        verify(jwtProvider).generateToken(user.getEmail());
    }

    @Test
    void register_ThrowsException_WhenEmailExists() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        EntityAlreadyExistsException exception = assertThrows(EntityAlreadyExistsException.class, () -> {
            authService.register(user);
        });

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository).existsByEmail(user.getEmail());
        verifyNoInteractions(addressRepository, passwordEncoder, jwtProvider);
    }

    @Test
    void register_ThrowsException_WhenBsnExists() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByBsnNumber(user.getBsnNumber())).thenReturn(true);

        EntityAlreadyExistsException exception = assertThrows(EntityAlreadyExistsException.class, () -> {
            authService.register(user);
        });

        assertEquals("BSN already exists", exception.getMessage());
        verify(userRepository).existsByBsnNumber(user.getBsnNumber());
        verifyNoInteractions(addressRepository, passwordEncoder, jwtProvider);
    }

    @Test
    void register_Success_WithExistingAddress() {
        address.setId(1L);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByBsnNumber(user.getBsnNumber())).thenReturn(false);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(jwtProvider.generateToken(user.getEmail())).thenReturn("mockedToken");

        String token = authService.register(user);

        assertEquals("mockedToken", token);
        verify(addressRepository, never()).save(any());
    }
}
