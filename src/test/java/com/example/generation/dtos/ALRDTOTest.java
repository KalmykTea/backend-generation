package com.example.generation.dtos;

import com.example.generation.dtos.RequestDTOs.AccountLimitsRequestDTO;
import com.example.generation.framework.groups.OnUpdate;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ALRDTOTest {
    AccountLimitsRequestDTO validALRDto;
    AccountLimitsRequestDTO invalidALRDto;
    String validIBAN;
    String invalidIBAN;
    BigDecimal validDailyLimit;
    BigDecimal validAbsoluteLimit;
    BigDecimal invalidDailyLimit;
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeEach
    public void setUp(){
        validALRDto = new AccountLimitsRequestDTO();
        invalidALRDto = new AccountLimitsRequestDTO();
        validIBAN = "NL62INHO0366278277";
        invalidIBAN = "123456789ctu";
        validDailyLimit = BigDecimal.valueOf(10);
        invalidDailyLimit = BigDecimal.valueOf(-20);
        validAbsoluteLimit = BigDecimal.valueOf(-10);
        validALRDto.setIban(validIBAN);
        invalidALRDto.setIban(invalidIBAN);
        validALRDto.setDailyLimit(validDailyLimit);
        invalidALRDto.setDailyLimit(invalidDailyLimit);
        validALRDto.setAbsoluteLimit(validAbsoluteLimit);
    }

    @Test
    void ALRDTO_hasNoIbanViolations(){
        Set<ConstraintViolation<AccountLimitsRequestDTO>> violations = validator.validateProperty(validALRDto, "iban", OnUpdate.class);
        assertTrue(violations.isEmpty());
    }

    @Test
    void ALRDTO_hasIbanViolations(){
        Set<ConstraintViolation<AccountLimitsRequestDTO>> violations = validator.validateProperty(invalidALRDto, "iban", OnUpdate.class);
        assertFalse(violations.isEmpty());
    }

    @Test
    void ALRDTO_hasNoDailyLimitViolations(){
        Set<ConstraintViolation<AccountLimitsRequestDTO>> violations = validator.validateProperty(validALRDto, "dailyLimit", OnUpdate.class);
        assertTrue(violations.isEmpty());

    }

    @Test
    void ALRDTO_hasDailyLimitViolations(){
        Set<ConstraintViolation<AccountLimitsRequestDTO>> violations = validator.validateProperty(invalidALRDto, "dailyLimit", OnUpdate.class);
        assertFalse(violations.isEmpty());
    }

    @Test
    void ALRDTO_hasNoViolations(){
        Set<ConstraintViolation<AccountLimitsRequestDTO>> violations = validator.validate(validALRDto, OnUpdate.class);
        assertTrue(violations.isEmpty());
    }

    @Test
    void ALRDTO_hasViolations(){
        Set<ConstraintViolation<AccountLimitsRequestDTO>> violations = validator.validate(invalidALRDto,  OnUpdate.class);
        assertFalse(violations.isEmpty());
    }
}
