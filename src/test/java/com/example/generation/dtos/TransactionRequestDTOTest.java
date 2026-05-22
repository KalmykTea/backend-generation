package com.example.generation.dtos;

import com.example.generation.dtos.RequestDTOs.BaseTransactionRequestDTO;
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
        validDTO = (TransactionRequestDTO) wholeValidDTO;
        validDTO.setFromAccountIban("NL62INHO0366278277");
        validDTO.setToAccountIban("NL32INHO0377278277");
        invalidDTO = (TransactionRequestDTO) wholeInvalidDTO;
        invalidDTO.setFromAccountIban("123456789");
        invalidDTO.setToAccountIban("129056789");
    }

    @Override
    BaseTransactionRequestDTO createDTO() {
        return new TransactionRequestDTO();
    }

    @Test
    void TRDTO_hasNoFromAccountIbanViolations()
    {
        Set<ConstraintViolation<TransactionRequestDTO>> violations = validator.validateProperty(validDTO,  "fromAccountIban");
        assertTrue(violations.isEmpty());
    }

    @Test
    void TRDTO_hasFromAccountIbanViolations()
    {
        Set<ConstraintViolation<TransactionRequestDTO>> violations = validator.validateProperty(invalidDTO,   "fromAccountIban");
        assertFalse(violations.isEmpty());
    }

    @Test
    void TRDTO_hasNoToAccountIbanViolations()
    {
        Set<ConstraintViolation<TransactionRequestDTO>> violations = validator.validateProperty(validDTO, "toAccountIban");
        assertTrue(violations.isEmpty());
    }

    @Test
    void TRDTO_hasToAccountIbanViolations()
    {
        Set<ConstraintViolation<TransactionRequestDTO>> violations = validator.validateProperty(invalidDTO, "toAccountIban");
        assertFalse(violations.isEmpty());
    }

    @Test
    void TRDTO_hasNoViolations(){
        Set<ConstraintViolation<TransactionRequestDTO>> violations = validator.validate(validDTO);
        assertTrue(violations.isEmpty());
    }

    @Test
    void TRDTO_hasViolations(){
        Set<ConstraintViolation<TransactionRequestDTO>> violations = validator.validate(invalidDTO);
        assertFalse(violations.isEmpty());
    }

}
