package com.example.generation.dtos;

import com.example.generation.dtos.RequestDTOs.TransactionRequestDTO;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransactionRequestDTOTest extends BTRDTOTest{
    TransactionRequestDTO validDTO;
    TransactionRequestDTO invalidDTO;

    @BeforeEach
    void setUp()
    {
        super.setUp();
        validDTO = new TransactionRequestDTO();
        validDTO.setFromAccountIban("NL62INHO0366278277");
        validDTO.setToAccountIban("NL32INHO0377278277");
        invalidDTO = new TransactionRequestDTO();
        invalidDTO.setFromAccountIban("123456789");
        invalidDTO.setToAccountIban("129056789");
    }

    @Test
    void TRDTO_hasNoFromAccountIbanViolations()
    {
        Set<ConstraintViolation<TransactionRequestDTO>> violations = validator.validate(validDTO);
        boolean hasIbanViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("fromAccountIban"));
        assertFalse(hasIbanViolation);
    }

    @Test
    void TRDTO_hasFromAccountIbanViolations()
    {
        Set<ConstraintViolation<TransactionRequestDTO>> violations = validator.validate(invalidDTO);
        boolean hasIbanViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("fromAccountIban"));
        assertTrue(hasIbanViolation);
    }

    @Test
    void TRDTO_hasNoToAccountIbanViolations()
    {
        Set<ConstraintViolation<TransactionRequestDTO>> violations = validator.validate(validDTO);
        boolean hasIbanViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("toAccountIban"));
        assertFalse(hasIbanViolation);
    }

    @Test
    void TRDTO_hasToAccountIbanViolations()
    {
        Set<ConstraintViolation<TransactionRequestDTO>> violations = validator.validate(invalidDTO);
        boolean hasIbanViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("toAccountIban"));
        assertTrue(hasIbanViolation);
    }


}
