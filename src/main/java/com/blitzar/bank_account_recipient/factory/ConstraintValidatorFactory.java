package com.blitzar.bank_account_recipient.factory;

import com.blitzar.bank_account_recipient.validation.annotation.Iban;
import com.blitzar.bank_account_recipient.validation.annotation.SecureInput;
import com.blitzar.bank_account_recipient.validation.annotation.validator.IbanValidator;
import com.blitzar.bank_account_recipient.validation.annotation.validator.SecureInputValidator;
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
    ConstraintValidator<SecureInput, String> secureInputValidator() {
        return (value, annotationMetadata, context) -> new SecureInputValidator().isValid(value, context);
    }
}
