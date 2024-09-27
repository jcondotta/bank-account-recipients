package com.blitzar.bank_account_recipient.validation.f;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { MaliciousInputValidator.class })
@Documented
public @interface NoMaliciousInput {
    String message() default "Invalid input, potential malicious code detected";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}