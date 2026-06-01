package com.example.generation.framework.annotations;

import com.example.generation.framework.validators.BirthDateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.lang.annotation.*;

@NotNull
@Past(message = "Birthdate must be in the past")
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {BirthDateValidator.class})
@Documented
public @interface ValidBirthDate {
    String message() default "{birthdate.invalid}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
