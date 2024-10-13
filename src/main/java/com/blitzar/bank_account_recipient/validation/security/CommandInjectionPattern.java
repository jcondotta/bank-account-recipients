package com.blitzar.bank_account_recipient.validation.security;

import java.util.regex.Pattern;

public class CommandInjectionPattern implements ThreatInputPattern {

    private static final Pattern COMMAND_INJECTION_PATTERN = Pattern.compile(
            "(;.*?rm\\b)|" +                                // Command injection: rm
            "(&&.*?ls)|" +                                  // Listing files
            "(\\|.*?cat\\b)|" +                             // Piping output
            "(&&[^>]*+>)|" +                                // Redirecting output (retain possessive quantifier here)
            "(&&.*?&)|" +                                   // Background execution
            "(;.*?ls)|" +                                   // Multiple commands
            "(\\|\\|\\s*true\\b)|" +                        // Bypassing with || true
            "(\\bsh\\b|\\bchmod\\b|\\bchown\\b)",           // Shell commands
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public boolean containsPattern(String value) {
        return COMMAND_INJECTION_PATTERN.matcher(value).find();
    }
}
