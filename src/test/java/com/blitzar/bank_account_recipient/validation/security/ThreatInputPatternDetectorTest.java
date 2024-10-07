package com.blitzar.bank_account_recipient.validation.security;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ThreatInputPatternDetectorTest {

    private final ThreatInputPatternDetector detector = new ThreatInputPatternDetector();

    @Test
    public void shouldDetectXSSInDetector() {
        String maliciousInput = "<script>alert('XSS')</script>";
        assertTrue(detector.containsAnyPattern(maliciousInput));
    }

    @Test
    public void shouldDetectSQLInjectionInDetector() {
        String maliciousInput = "SELECT * FROM users WHERE id = 1 OR 1=1 --";
        assertTrue(detector.containsAnyPattern(maliciousInput));
    }

    @Test
    public void shouldDetectCommandInjectionInDetector() {
        String maliciousInput = "rm -rf / && ls";
        assertTrue(detector.containsAnyPattern(maliciousInput));
    }

    @Test
    public void shouldNotDetectSafeInput() {
        String safeInput = "Hello World";
        assertFalse(detector.containsAnyPattern(safeInput));
    }

    @Test
    public void shouldUseCustomPatterns() {
        ThreatInputPattern customPattern = value -> value.contains("custom");
        ThreatInputPatternDetector customDetector = new ThreatInputPatternDetector(List.of(customPattern));

        assertTrue(customDetector.containsAnyPattern("this is a custom input"));
        assertFalse(customDetector.containsAnyPattern("this is safe"));
    }
}