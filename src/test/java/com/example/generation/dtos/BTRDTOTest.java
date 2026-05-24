package com.example.generation.dtos;

import com.example.generation.dtos.RequestDTOs.BaseTransactionRequestDTO;
import com.example.generation.enums.TransactionType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BTRDTOTest {
    BaseTransactionRequestDTO wholeValidDTO;
    BaseTransactionRequestDTO wholeInvalidDTO;
    String validDescription;
    String invalidDescription;
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeEach
    void setUp() {
        validDescription = null;
        invalidDescription = "The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog.";
        wholeInvalidDTO = createDTO();
        wholeInvalidDTO.setAmount(BigDecimal.valueOf(-100.986));
        wholeInvalidDTO.setDescription(invalidDescription);

        wholeValidDTO = createDTO();
        wholeValidDTO.setAmount(BigDecimal.valueOf(100));
        wholeValidDTO.setDescription(null);
        wholeValidDTO.setTransactionType(TransactionType.TRANSFER);
    }

     BaseTransactionRequestDTO createDTO() {
        return new BaseTransactionRequestDTO();
    }

    @Test
    void BTRDTO_hasNoAmountViolations()
    {
        Set<ConstraintViolation<BaseTransactionRequestDTO>> violations = validator.validateProperty(wholeValidDTO, "amount");
        assertTrue(violations.isEmpty());
    }

    @Test
    void BTRDTO_hasAmountViolations(){
        Set<ConstraintViolation<BaseTransactionRequestDTO>> violations = validator.validateProperty(wholeInvalidDTO, "amount");
        assertFalse(violations.isEmpty());
    }

    @Test
    void BTRDTO_hasDescriptionViolations(){

        Set<ConstraintViolation<BaseTransactionRequestDTO>> violations = validator.validateProperty(wholeInvalidDTO, "description");
        assertFalse(violations.isEmpty());
    }

    @Test
    void BTRDTO_hasNoDescriptionViolations(){
        Set<ConstraintViolation<BaseTransactionRequestDTO>> violations = validator.validateProperty(wholeValidDTO, "description");
        assertTrue(violations.isEmpty());
    }

    @Test
    void BTRDTO_hasNoViolations(){
        Set<ConstraintViolation<BaseTransactionRequestDTO>> violations = validator.validate(wholeValidDTO);
        assertTrue(violations.isEmpty());
    }

    @Test
    void BTRDTO_hasViolations(){
        Set<ConstraintViolation<BaseTransactionRequestDTO>> violations = validator.validate(wholeInvalidDTO);
        assertFalse(violations.isEmpty());
    }
}
