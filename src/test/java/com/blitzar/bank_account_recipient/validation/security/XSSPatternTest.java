package com.blitzar.bank_account_recipient.validation.security;

import com.blitzar.bank_account_recipient.validation.security.XSSPattern;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XSSPatternTest {

    private final XSSPattern xssPattern = new XSSPattern();

    @Test
    public void shouldDetectXSSScriptTag() {
        String maliciousInput = "<script>alert('XSS')</script>";
        assertTrue(xssPattern.containsPattern(maliciousInput));
    }

    @Test
    public void shouldDetectEncodedXSSScriptTag() {
        String maliciousInput = "%3Cscript%3Ealert('XSS')%3C/script%3E";
        assertTrue(xssPattern.containsPattern(maliciousInput));
    }

    @Test
    public void shouldNotDetectNonXSSInput() {
        String safeInput = "<p>Hello World!</p>";
        assertFalse(xssPattern.containsPattern(safeInput));
    }
}
