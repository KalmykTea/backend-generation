package com.example.generation.framework.annotations;

import com.example.generation.framework.validators.IBANValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.lang.annotation.*;

@NotBlank
@Pattern(regexp = "[A-Z]{2}[0-9]{2}[A-Z0-9]{11,30}")
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {IBANValidator.class})
@Documented
public @interface ValidIBAN {
    String message() default "{iban.invalid}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
