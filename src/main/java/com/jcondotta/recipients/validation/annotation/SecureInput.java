package com.jcondotta.recipients.validation.annotation;

import com.jcondotta.recipients.validation.annotation.validator.SecureInputValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE_USE, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { SecureInputValidator.class })
@Documented
public @interface SecureInput {
    String message() default "Invalid input, potential malicious code detected";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}