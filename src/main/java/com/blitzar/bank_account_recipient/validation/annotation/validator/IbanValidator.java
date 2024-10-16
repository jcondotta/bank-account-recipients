
package com.blitzar.bank_account_recipient.validation.annotation.validator;

import com.blitzar.bank_account_recipient.validation.annotation.Iban;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.validation.validator.constraints.ConstraintValidator;
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext;
import jakarta.inject.Singleton;
import org.apache.commons.validator.routines.IBANValidator;

@Singleton
public class IbanValidator implements ConstraintValidator<Iban, String> {

    private final IBANValidator apacheIbanValidator = new IBANValidator();

    @Override
    public boolean isValid(@Nullable String value, @NonNull AnnotationValue<Iban> annotationMetadata, @NonNull ConstraintValidatorContext context) {
        var customMessage = annotationMetadata.get("message", String.class, "recipient.recipientIban.invalid");

        var sanitizedIban = value == null ? "" : value.replaceAll("\\s+", "").trim();

        if (apacheIbanValidator.isValid(sanitizedIban)) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(customMessage).addConstraintViolation();
        return false;
    }
}
