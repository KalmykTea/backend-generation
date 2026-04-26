package com.example.generation.framework.validators;

import com.example.generation.framework.annotations.ValidBSN;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BSNValidator implements ConstraintValidator<ValidBSN, String> {

    @Override
    public boolean isValid(String bsn, ConstraintValidatorContext context) {
        if (bsn == null) return true;
        return isValidBSN(bsn);
    }

    private boolean isValidBSN(String bsn) {
        if (bsn.length() != 9) return false;

        int[] multipliers = {9, 8, 7, 6, 5, 4, 3, 2, -1};
        int sum = 0;
        for (int i = 0; i < multipliers.length; i++) {
            sum += Character.getNumericValue(bsn.charAt(i)) * multipliers[i];
        }
        return sum % 11 == 0;
    }
}
