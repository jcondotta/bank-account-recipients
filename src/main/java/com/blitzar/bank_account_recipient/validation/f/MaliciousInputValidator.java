package com.blitzar.bank_account_recipient.validation.f;

import java.util.regex.Pattern;

import com.blitzar.bank_account_recipient.validation.f.NoMaliciousInput;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.validation.validator.constraints.ConstraintValidator;
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext;

public class MaliciousInputValidator implements ConstraintValidator<NoMaliciousInput, String> {

    // Updated XSS Pattern to catch encoded and specific edge cases
    private static final Pattern XSS_PATTERN = Pattern.compile(
            "(<script.*?>.*?</script.*?>)|" +                  // XSS <script> tag
                    "(%3C.*?script.*?%3E)|" +                          // Encoded <script>
                    "(<img.*?onerror.*?>)|" +                          // XSS Image tag
                    "(\".*?on.*?=.*?\")|" +                            // XSS Attribute injection
                    "(javascript:.*?alert\\(.*?\\))|" +                // Inline JS execution
                    "(onload=alert\\(.*?\\))|" +                       // XSS event handler
                    "(<!--.*?<script.*?>.*?</script.*?>.*?-->)|" +     // XSS in HTML comments
                    "(%3C.*?img.*?onerror.*?%3E)|" +                   // Encoded XSS in image tag
                    "(background-image:\\s*url\\(.*?javascript:.*?\\))", // XSS in CSS
            Pattern.CASE_INSENSITIVE
    );

    // Updated SQL Injection Pattern to catch more SQL cases
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
            "('.+--)|" +                                       // SQL comment with single quote
                    "(--.*?$)|" +                                      // SQL comment
                    "(\\b(SELECT|UPDATE|DELETE|INSERT|UNION|DROP)\\b)|" + // SQL keywords
                    "(\\bOR\\b\\s+\\d+\\s*=\\s*\\d+)|" +               // SQL OR condition like "OR 1=1"
                    "(\\bOR\\b\\s+'.*?'\\s*=\\s*'.*?')|" +             // SQL OR with string values
                    "(;.*?--)|" +                                      // SQL commented out code
                    "(WAITFOR\\s+DELAY)|" +                            // SQL Time delay attack
                    "(\\bUNION\\b.*?\\bSELECT\\b)",                    // SQL Union select
            Pattern.CASE_INSENSITIVE
    );

    // Updated Command Injection Pattern to catch more cases
    private static final Pattern COMMAND_INJECTION_PATTERN = Pattern.compile(
            "(;.*?rm\\b)|" +                                   // Command injection: rm
                    "(&&.*?ls)|" +                                     // Listing files
                    "(\\|.*?cat\\b)|" +                                // Piping output
                    "(&&.*?>)|" +                                      // Redirecting output
                    "(&&.*?&)|" +                                      // Background execution
                    "(;.*?ls)|" +                                      // Multiple commands
                    "(\\|\\|\\s*true\\b)|" +                           // Bypassing with || true
                    "(\\bsh\\b|\\bchmod\\b|\\bchown\\b)",              // Shell commands
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public boolean isValid(@Nullable String value, @NonNull AnnotationValue<NoMaliciousInput> annotationMetadata, @NonNull ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // No input to validate, so it's valid
        }

        // Check for XSS, SQL Injection, and Command Injection patterns
        if (XSS_PATTERN.matcher(value).find() ||
                SQL_INJECTION_PATTERN.matcher(value).find() ||
                COMMAND_INJECTION_PATTERN.matcher(value).find()) {
            return false;
        }

        return true;
    }
}
