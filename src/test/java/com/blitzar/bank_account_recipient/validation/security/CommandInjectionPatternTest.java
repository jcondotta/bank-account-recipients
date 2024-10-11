package com.blitzar.bank_account_recipient.validation.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandInjectionPatternTest {

    private final CommandInjectionPattern commandInjectionPattern = new CommandInjectionPattern();

    @Test
    void shouldDetectCommandInjection() {
        String maliciousInput = "rm -rf / && ls";
        assertTrue(commandInjectionPattern.containsPattern(maliciousInput));
    }

    @Test
    void shouldDetectShellCommandInjection() {
        String maliciousInput = "sh -c 'ls -la'";
        assertTrue(commandInjectionPattern.containsPattern(maliciousInput));
    }

    @Test
    void shouldNotDetectNonCommandInjection() {
        String safeInput = "echo 'Hello World'";
        assertFalse(commandInjectionPattern.containsPattern(safeInput));
    }
}
