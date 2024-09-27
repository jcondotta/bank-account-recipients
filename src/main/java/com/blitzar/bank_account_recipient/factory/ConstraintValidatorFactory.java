package com.blitzar.bank_account_recipient.factory;

import com.blitzar.bank_account_recipient.validation.Iban;
import com.blitzar.bank_account_recipient.validation.IbanValidator;
import com.blitzar.bank_account_recipient.validation.f.MaliciousInputValidator;
import com.blitzar.bank_account_recipient.validation.f.NoMaliciousInput;
import io.micronaut.context.annotation.Factory;
import io.micronaut.validation.validator.constraints.ConstraintValidator;
import jakarta.inject.Singleton;

@Factory
public class ConstraintValidatorFactory {

    @Singleton
    ConstraintValidator<Iban, String> ibanValidator() {
        return (value, annotationMetadata, context) -> new IbanValidator().isValid(value, context);
    }

    @Singleton
    ConstraintValidator<NoMaliciousInput, String> maliciousInputValidator() {
        return (value, annotationMetadata, context) -> new MaliciousInputValidator().isValid(value, context);
    }
}
