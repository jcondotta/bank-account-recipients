package com.blitzar.bank_account_recipient.validation;

import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.validation.validator.constraints.ConstraintValidator;
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext;
import org.apache.commons.validator.routines.IBANValidator;

public class InvalidIbanValidator implements ConstraintValidator<Iban, String> {

    private final IBANValidator ibanValidator = new IBANValidator();

    @Override
    public boolean isValid(@Nullable String value, @NonNull AnnotationValue<Iban> annotationMetadata, @NonNull ConstraintValidatorContext context) {
        String ibanWithoutSpaces = value == null ? "" : value.replaceAll("\\s+", "").trim();
        boolean isValidIban = ibanValidator.isValid(ibanWithoutSpaces);

        if (!isValidIban) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("recipient.recipientIban.invalid").addConstraintViolation();
            return false;
        }

        return true;
    }

    public static void main(String[] args) {
    }

//    private final IBANValidator ibanValidator = new IBANValidator();
//    private final InvalidStringValidator invalidStringValidator = new InvalidStringValidator();
//
//    @Override
//    public boolean isValid(String iban, ConstraintValidatorContext context) {
//        boolean isInvalidString = !invalidStringValidator.isValid(iban, context);
//        if (isInvalidString) {
//            context.disableDefaultConstraintViolation();
//            context.buildConstraintViolationWithTemplate("recipient.recipientIban.notBlank")
//                    .addConstraintViolation();
//            return false;
//        }
//
//        String ibanWithoutSpaces = iban == null ? "" : iban.replaceAll("\\s+", "").trim();
//        boolean isValidIban = ibanValidator.isValid(ibanWithoutSpaces);
//
//        if (!isValidIban) {
//            context.disableDefaultConstraintViolation();
//            context.buildConstraintViolationWithTemplate("recipient.recipientIban.invalid")
//                    .addConstraintViolation();
//            return false;
//        }
//
//        return true;
//    }
}