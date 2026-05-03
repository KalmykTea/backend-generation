package com.example.generation.framework.annotations;

import com.example.generation.framework.validators.BSNValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.*;

@NotBlank
@Pattern(regexp = "^\\d{8,9}$", message = "BSN must be 8 or 9 digits")
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {BSNValidator.class})
@Documented
public @interface ValidBSN {
    String message() default "{bsn.invalid}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
