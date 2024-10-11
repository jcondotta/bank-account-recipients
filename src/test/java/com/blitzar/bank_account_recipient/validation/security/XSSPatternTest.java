package com.blitzar.bank_account_recipient.validation.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class XSSPatternTest {

    private final XSSPattern xssPattern = new XSSPattern();

    @Test
    void shouldDetectXSSScriptTag() {
        String maliciousInput = "<script>alert('XSS')</script>";
        assertTrue(xssPattern.containsPattern(maliciousInput));
    }

    @Test
    void shouldDetectEncodedXSSScriptTag() {
        String maliciousInput = "%3Cscript%3Ealert('XSS')%3C/script%3E";
        assertTrue(xssPattern.containsPattern(maliciousInput));
    }

    @Test
    void shouldNotDetectNonXSSInput() {
        String safeInput = "<p>Hello World!</p>";
        assertFalse(xssPattern.containsPattern(safeInput));
    }
}
