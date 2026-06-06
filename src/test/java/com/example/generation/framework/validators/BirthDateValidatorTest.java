package com.example.generation.framework.validators;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class BirthDateValidatorTest {

    private BirthDateValidator validator;
    @BeforeEach
    void setUp() {
        validator = new BirthDateValidator();
    }

    @Test
    void isValid_ShouldReturnTrue_WhenDateIsNull() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void isValid_ShouldReturnTrue_WhenUserIsAtLeast18() {
        LocalDate eighteenYearsAgo = LocalDate.now().minusYears(18);
        assertTrue(validator.isValid(eighteenYearsAgo, null));

        LocalDate olderThanEighteen = LocalDate.now().minusYears(25);
        assertTrue(validator.isValid(olderThanEighteen, null));
    }

    @Test
    void isValid_ShouldReturnFalse_WhenDateIsFuture() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        assertFalse(validator.isValid(futureDate, null));
    }
}
