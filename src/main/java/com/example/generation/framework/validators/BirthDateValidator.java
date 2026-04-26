package com.example.generation.framework.validators;

import com.example.generation.framework.annotations.ValidBirthDate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class BirthDateValidator implements ConstraintValidator<ValidBirthDate, LocalDate> {
    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext constraintValidatorContext) {
        if (date == null) return true;

        boolean isValid = !date.isAfter(LocalDate.now().minusYears(18));

        if (!isValid) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Must be at least 18")
                    .addConstraintViolation();
        }
        return isValid;
    }
}
