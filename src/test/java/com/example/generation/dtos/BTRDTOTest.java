package com.example.generation.dtos;

import com.example.generation.dtos.RequestDTOs.BaseTransactionRequestDTO;
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
    BaseTransactionRequestDTO dto;
    BaseTransactionRequestDTO dto2;
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeEach
    void setUp() {
        dto = createDTO();
        dto.setAmount(BigDecimal.valueOf(100));
        dto.setDescription("The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog.");
        dto2 = createDTO();
        dto2.setAmount(BigDecimal.valueOf(-100.986));
        dto2.setDescription(null);
    }

     BaseTransactionRequestDTO createDTO() {
        return new BaseTransactionRequestDTO();
    }

    @Test
    void BTRDTO_hasNoAmountViolations()
    {
        Set<ConstraintViolation<BaseTransactionRequestDTO>> violations = validator.validateProperty(dto, "amount");
        assertTrue(violations.isEmpty());
    }

    @Test
    void BTRDTO_hasAmountViolations(){
        Set<ConstraintViolation<BaseTransactionRequestDTO>> violations = validator.validateProperty(dto2, "amount");
        assertFalse(violations.isEmpty());
    }

    @Test
    void BTRDTO_hasDescriptionViolations(){

        Set<ConstraintViolation<BaseTransactionRequestDTO>> violations = validator.validateProperty(dto, "description");
        assertFalse(violations.isEmpty());
    }

    @Test
    void BTRDTO_hasNoDescriptionViolations(){
        Set<ConstraintViolation<BaseTransactionRequestDTO>> violations = validator.validateProperty(dto2, "description");
        assertTrue(violations.isEmpty());
    }
}
