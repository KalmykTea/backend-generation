package com.example.generation.dtos;

import com.example.generation.dtos.RequestDTOs.ATMRequestDTO;
import com.example.generation.dtos.RequestDTOs.BaseTransactionRequestDTO;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ATMRequestDTOTest extends BTRDTOTest{
    ATMRequestDTO validAtmDto;
    ATMRequestDTO invalidAtmDto;
    String validIBAN;
    String invalidIBAN;

    @Override
    BaseTransactionRequestDTO createDTO() {
        return new ATMRequestDTO();
    }

    @BeforeEach
    void setUp() {
        super.setUp();
        validAtmDto = new ATMRequestDTO();
        invalidAtmDto = new ATMRequestDTO();
        validIBAN = "NL62INHO0366278277";
        invalidIBAN = "123456789ctu";
        validAtmDto.setIban(validIBAN);
        invalidAtmDto.setIban(invalidIBAN);
    }

    @Test
    void ATMRDTO_hasNoIbanViolations(){
        Set<ConstraintViolation<ATMRequestDTO>> violations = validator.validate(validAtmDto);
        boolean hasIbanViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("iban"));
        assertFalse(hasIbanViolation);
    }

    @Test
    void ATMRDTO_hasIbanViolations(){
        Set<ConstraintViolation<ATMRequestDTO>> violations = validator.validate(invalidAtmDto);
        boolean hasIbanViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("iban"));
        assertTrue(hasIbanViolation);
    }
}
