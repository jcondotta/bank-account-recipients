package com.jcondotta.recipients.validation.annotation;

import com.jcondotta.recipients.validation.annotation.validator.IbanValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IbanValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Iban {

    String message() default "recipient.recipientIban.invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}