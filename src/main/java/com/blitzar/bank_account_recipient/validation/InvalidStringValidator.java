package com.blitzar.bank_account_recipient.validation;

import jakarta.inject.Singleton;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Singleton
public class InvalidStringValidator implements ConstraintValidator<InvalidString, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return false; // handled by @NotBlank
        }

        if (containsControlCharacters(value)) {
            return false; // check for control characters
        }

        if (containsXSS(value)) {
            return false; // check for XSS patterns
        }

        if (containsSQLInjection(value)) {
            return false; // check for SQL injection patterns
        }

        if (containsCommandInjection(value)) {
            return false; // check for command injection patterns
        }

        return true; // Value is valid
    }

    private boolean containsXSS(String value) {
        String[] xssPatterns = {
                "onload=", "onclick=", "onerror=", "onmouseover=", "alert(", "<script>", "<img", "<iframe", "<a"
        };

        for (String pattern : xssPatterns) {
            if (value.toLowerCase().contains(pattern)) {
                return true; // found an XSS pattern
            }
        }

        // Check for encoded XSS patterns
        return value.contains("%3C") || value.contains("%3E") || value.contains("%22");
    }

    private boolean containsSQLInjection(String value) {
        // Check for common SQL injection patterns using regex
        String regex = "(?i)\\b(SELECT|INSERT|UPDATE|DELETE|DROP|UNION|OR|AND|WHERE|HAVING|LIKE|SET|EXEC|VALUES)\\b";

        // Check for patterns that typically indicate SQL injection attempts
        String[] sqlInjectionPatterns = {
                "' OR '1'='1'",
                "1; DROP TABLE",
                "UNION SELECT",
                "OR 1=1",
                "SELECT \\* FROM",
                "WAITFOR DELAY",
                "SELECT COUNT\\(\\*\\)", // This can catch counts but might not catch nested SELECTs.
                "--", // SQL comments
                "1; SELECT ", // A simple check for the presence of nested SELECTs
                "SELECT\\s*\\(SELECT" // Pattern for nested SELECT queries
        };

        for (String pattern : sqlInjectionPatterns) {
            if (value.matches(".*" + pattern + ".*")) {
                return true; // found an SQL injection pattern
            }
        }

        // Check for more complex cases with regex
        if (value.matches(regex)) {
            return true; // found a SQL command
        }

        return false; // no SQL injection patterns found
    }

    private boolean containsCommandInjection(String value) {
        String[] commandInjectionPatterns = {
                "; rm -rf", "&& ls", "| cat", "&& echo", "&& sleep", "&& echo 'test';"
        };

        for (String pattern : commandInjectionPatterns) {
            if (value.contains(pattern)) {
                return true; // found a command injection pattern
            }
        }

        return false; // no command injection patterns found
    }

    private boolean containsControlCharacters(String value) {
        // Check for control characters (ASCII 0-31 and 127)
        return value.chars().anyMatch(c -> (c < 32 || c == 127));
    }

}