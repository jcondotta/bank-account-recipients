package com.blitzar.bank_account_recipient.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = InvalidIbanValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Iban {

    String message() default "recipient.recipientIban.invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}