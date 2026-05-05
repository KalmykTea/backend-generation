package com.example.generation.framework.annotations;

import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.*;

@NotBlank
@Pattern(regexp = "NL\\d{2}INHO0\\d{9}")
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidIBAN {
    String message() default "{iban.invalid}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
