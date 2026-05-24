package com.example.generation.dtos;

import com.example.generation.dtos.RequestDTOs.ATMRequestDTO;
import com.example.generation.dtos.RequestDTOs.BaseTransactionRequestDTO;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        validAtmDto = (ATMRequestDTO) wholeValidDTO;
        invalidAtmDto = (ATMRequestDTO) wholeInvalidDTO;
        validIBAN = "NL62INHO0366278277";
        invalidIBAN = "123456789ctu";
        validAtmDto.setIban(validIBAN);
        invalidAtmDto.setIban(invalidIBAN);
    }

    @Test
    void ATMRDTO_hasNoIbanViolations(){
        Set<ConstraintViolation<ATMRequestDTO>> violations = validator.validateProperty(validAtmDto, "iban");
        assertTrue(violations.isEmpty());
    }

    @Test
    void ATMRDTO_hasIbanViolations(){
        Set<ConstraintViolation<ATMRequestDTO>> violations = validator.validateProperty(invalidAtmDto, "iban");
        assertFalse(violations.isEmpty());
    }

    @Test
    void ATMRDTO_hasNoViolations() {
        Set<ConstraintViolation<ATMRequestDTO>> violations = validator.validate(validAtmDto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void ATMRDTO_hasViolations() {
        Set<ConstraintViolation<ATMRequestDTO>> violations = validator.validate(invalidAtmDto);
        assertFalse(violations.isEmpty());
    }
}
