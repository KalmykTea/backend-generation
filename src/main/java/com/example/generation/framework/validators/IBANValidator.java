package com.example.generation.framework.validators;

import com.example.generation.framework.annotations.ValidIBAN;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IBANValidator implements ConstraintValidator<ValidIBAN, String> {

    @Override
    public boolean isValid(String iban, ConstraintValidatorContext context) {
        if (iban == null) return true;

        String normalized = iban.replaceAll("\\s+", "").toUpperCase();
        if (normalized.length() < 4) return false;

        String rearranged = normalized.substring(4) + normalized.substring(0, 4);

        StringBuilder numeric = new StringBuilder();
        for (char ch : rearranged.toCharArray()) {
            numeric.append(Character.isLetter(ch) ? (ch - 'A' + 10) : ch);
        }

        return mod97(numeric.toString()) == 1;
    }

    private static int mod97(String number) {
        long remainder = 0;
        for (int i = 0; i < number.length(); i += 9) {
            String chunk = remainder + number.substring(i, Math.min(i + 9, number.length()));
            remainder = Long.parseLong(chunk) % 97;
        }
        return (int) remainder;
    }
}
