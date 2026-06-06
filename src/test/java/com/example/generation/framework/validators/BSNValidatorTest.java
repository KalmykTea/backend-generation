package com.example.generation.framework.validators;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class BSNValidatorTest {

    private BSNValidator validator;

    @BeforeEach
    void setUp() {
        validator = new BSNValidator();
    }

    @Test
    void isValid_ShouldReturnTrue_WhenBsnIsNull() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void isValid_ShouldReturnTrue_WhenBsnIsValid() {
        // Example of a valid BSN: 123456782
        // 9*1 + 8*2 + 7*3 + 6*4 + 5*5 + 4*6 + 3*7 + 2*8 + (-1)*2 = 
        // 9 + 16 + 21 + 24 + 25 + 24 + 21 + 16 - 2 = 154
        // 154 / 11 = 14.0 (Exactly divisible by 11)
        assertTrue(validator.isValid("123456782", null));
    }

    @Test
    void isValid_ShouldReturnFalse_WhenBsnIsInvalid() {
        // Invalid 11-test
        assertFalse(validator.isValid("123456783", null));
    }

    @Test
    void isValid_ShouldReturnFalse_WhenBsnLengthIsInvalid() {
        assertFalse(validator.isValid("12345678", null));
        assertFalse(validator.isValid("1234567890", null));
    }

    @Test
    void isValid_ShouldReturnFalse_WhenBsnContainsNonNumericCharacters() {
        // The implementation uses Character.getNumericValue which returns -1 for non-numeric.
        // -1 * multiplier will likely fail the 11-test, but it might even throw an exception if not careful.
        // Actually, Character.getNumericValue returns -1 or -2 for non-digits.
        // Let's see how it behaves.
        assertFalse(validator.isValid("12345678A", null));
    }
}
