package com.blitzar.bank_account_recipient.validation;

import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.validation.validator.constraints.ConstraintValidator;
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext;
import org.apache.commons.validator.routines.IBANValidator;

public class IbanValidator implements ConstraintValidator<Iban, String> {

    private final IBANValidator ibanValidator = new IBANValidator();

    @Override
    public boolean isValid(@Nullable String value, @NonNull AnnotationValue<Iban> annotationMetadata, @NonNull ConstraintValidatorContext context) {
        var customMessage = annotationMetadata.get("message", String.class, "recipient.recipientIban.invalid");

        var sanitizedIban = value == null ? "" : value.replaceAll("\\s+", "").trim();

        if (ibanValidator.isValid(sanitizedIban)) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(customMessage).addConstraintViolation();
        return false;
    }
}
