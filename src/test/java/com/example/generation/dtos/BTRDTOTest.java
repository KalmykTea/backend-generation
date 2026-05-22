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
        Set<ConstraintViolation<BaseTransactionRequestDTO>> violations = validator.validate(dto);
        boolean hasAmountViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("amount"));
        assertFalse(hasAmountViolation);
    }

    @Test
    void BTRDTO_hasAmountViolations(){
        Set<ConstraintViolation<BaseTransactionRequestDTO>> violations = validator.validate(dto2);
        boolean hasAmountViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("amount"));
        assertTrue(hasAmountViolation);
    }

    @Test
    void BTRDTO_hasDescriptionViolations(){

        Set<ConstraintViolation<BaseTransactionRequestDTO>> violations = validator.validate(dto);
        boolean hasDescriptionViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("description"));
        assertTrue(hasDescriptionViolation);
    }

    @Test
    void BTRDTO_hasNoDescriptionViolations(){
        Set<ConstraintViolation<BaseTransactionRequestDTO>> violations = validator.validate(dto2);
        boolean hasDescriptionViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("description"));
        assertFalse(hasDescriptionViolation);
    }
}
